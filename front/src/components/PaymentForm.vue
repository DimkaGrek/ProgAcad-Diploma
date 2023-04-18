<script>
import { useForm } from "../hooks/form";
import { computed, ref } from "vue";

const required = (val) => !!val;
const number = (val) => !isNaN(val);

export default {
  props: [
    "calcId",
    "amount",
    "counterId",
    "selectedCalcsForPayment",
    "overpayment",
    "calcsOverpayment",
  ],
  setup(props, { emit }) {
    console.log("props.calcId: ", props.calcId);
    console.log("props.amount: ", props.amount);
    console.log("props.counterId: ", props.counterId);
    console.log("props.overpayment: ", props.overpayment);
    console.log("calcsOverpayment: ", props.calcsOverpayment);

    const isCheckOverpayment = ref(false); // выбрана переплата или нет

    // const calcForPayment = ref(props.selectedCalcsForPayment);

    //определяем, массовая это оплата или нет
    const isMassivePayment = computed(() => {
      return props.selectedCalcsForPayment.length !== 0;
    });

    // формируем массив названий месяцев, за какие будет оплата
    const monthesPay = computed(() => {
      return isMassivePayment.value
        ? props.selectedCalcsForPayment.map((obj) => obj.month)
        : [];
    });

    // формируем массив ids, которую будут оплачены
    const idsPay = computed(() => {
      return isMassivePayment.value
        ? props.selectedCalcsForPayment.map((obj) => obj.id)
        : [];
    });

    const amountsPay = computed(() => {
      return isMassivePayment.value
        ? props.selectedCalcsForPayment.map((obj) => {
            return obj.payment === null ? obj.amount : obj.payment;
          })
        : [];
    });

    const now = new Date();
    const yearNow = now.getFullYear();
    const monthNow = now.getMonth() + 1;
    // console.log("monthNow: ", monthNow);
    // console.log("list month: ", month.value);
    const day = now.getDate();
    const hours = now.getHours();
    const minutes = now.getMinutes();
    const dateString = `${yearNow}-${monthNow.toString().padStart(2, "0")}-${day
      .toString()
      .padStart(2, "0")}T${hours.toString().padStart(2, "0")}:${minutes
      .toString()
      .padStart(2, "0")}`;

    const form = useForm({
      receiptId: {
        value: "",
        validators: { required, number },
      },
      amount: {
        value: props.amount,
        validators: { required, number },
      },
      date: {
        value: dateString,
        validators: { required },
      },
      notes: {
        value: isMassivePayment.value
          ? "Оплата за наступні місяці: " + monthesPay.value
          : "",
        validators: {},
      },
    });

    async function sendFetch(uri, formSend) {
      // отправляем данные на сервер
      await fetch(uri, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(formSend),
      })
        .then((response) => {
          if (response.ok) {
            //response.json()
            console.log("ОПЛАТА СОХРАНЕНА!");
            alert("Данні збережені успішно!");
            emit("submitForm", props.counterId);
          } else {
            // Обработка ошибки
            response.text().then((text) => {
              console.error("Ошибка " + response.status + " - " + text);
            });
            if (response.status == 409) {
              // state.existCounter = true;
              // form.number.valid = false;
            }
          }
        })
        .catch((error) => console.error("Ошибка сети: " + error));
    }

    function addPayment() {
      if (isMassivePayment.value) {
        console.log("ДОБАВЛЕНИЕ МАССОВОЙ ОПЛАТЫ!");
      } else {
        console.log("ДОБАВЛЕНИЕ ОПЛАТЫ!");
      }

      console.log(Object.keys(form));

      const formSend = Object.keys(form)
        .filter((k) => k !== "valid")
        .reduce((acc, key) => {
          acc[key] = form[key].value;
          return acc;
        }, {});
      formSend.date = formSend.date + ":00"; // add seconds to date
      formSend.counterId = props.counterId; // add counterId
      console.log("uri for massive payment: ", "api/payments/" + idsPay.value);
      if (isMassivePayment.value) {
        formSend.amountsPay = amountsPay.value; // добавляем список сумм каждой квитанции
        sendFetch("api/payments/" + idsPay.value, formSend);
      } else {
        if (isCheckOverpayment.value) {
          // если выбрана переплата, то отправляем на backend еще переплату и начисления, которые имеют переплату

          formSend.overpayment =
            props.overpayment > props.amount ? props.amount : props.overpayment;
          formSend.idsOverpayment = props.calcsOverpayment.map(
            (record) => record.id
          );
        }
        formSend.amountsPay = props.amount;
        sendFetch("api/payments/" + props.calcId, formSend);
      }
      console.log(JSON.stringify(formSend));
    }

    function selectOverpayment(event) {
      if (event.target.checked) {
        form.amount.value < props.overpayment
          ? ((form.amount.value = "0"), (form.receiptId.value = "0"))
          : (form.amount.value = form.amount.value - props.overpayment);

        isCheckOverpayment.value = true;
      } else {
        form.amount.value = props.amount;
        form.receiptId.value = "";
        isCheckOverpayment.value = false;
      }
    }

    return {
      form,
      addPayment,
      isMassivePayment,
      monthesPay,
      idsPay,
      amountsPay,
      selectOverpayment,
    };
  },
};
</script>

<template>
  <div class="add-new">
    <div class="add-header">
      <h1 v-if="isMassivePayment" class="ff-500-18">
        {{ $translate.t("makeMassivePayment") }}
      </h1>
      <h1 v-else class="ff-500-18">{{ $translate.t("makePayment") }}</h1>
      <small>( * - {{ $translate.t("requiredFields") }})</small>
      isMassivePayment: {{ isMassivePayment }}
    </div>
    <div class="add-main ff-500-14">
      <form @submit.prevent="">
        <div class="label">
          <label for="receiptId"
            >{{ $translate.t("formPaymentReceiptNum") }}
            <span class="indi-500">*</span></label
          >
        </div>
        <div class="input-div">
          <input
            v-model="form.receiptId.value"
            @blur="form.receiptId.blur"
            type="text"
            name="receiptId"
            class="ff-500-14"
            :class="{
              input: form.receiptId.valid || !form.receiptId.touched,
              invalid: !form.receiptId.valid && form.receiptId.touched,
            }"
          />
          <small
            v-if="form.receiptId.touched && form.receiptId.errors.required"
            >{{ $translate.t("formErrorPayRecNumReq") }}</small
          >
          <small
            v-else-if="form.receiptId.touched && form.receiptId.errors.number"
            >{{ $translate.t("formErrorPayRecIsNum") }}.</small
          >
        </div>

        <div class="label">
          <label for="amount"
            >{{ $translate.t("formPaymentAmount") }}
            <span class="indi-500">*</span></label
          >
        </div>
        <div class="input-div">
          <input
            v-model="form.amount.value"
            @blur="form.amount.blur"
            type="text"
            name="amount"
            class="ff-500-14"
            :class="{
              input: form.amount.valid || !form.amount.touched,
              invalid: !form.amount.valid && form.amount.touched,
            }"
          />
          <small v-if="form.amount.touched && form.amount.errors.required">{{
            $translate.t("formErrorPayAmountReq")
          }}</small>
          <small v-else-if="form.amount.touched && form.amount.errors.number"
            >{{ $translate.t("formErrorPayAmountNum") }}.</small
          >
          <div v-if="overpayment > 0 && !isMassivePayment">
            <label for="checkbox" class="checkbox-label">
              <input
                @change="selectOverpayment"
                id="checkbox"
                type="checkbox"
                class="checkbox-input"
              />
              <span class="checkbox-custom"></span>
            </label>
            <span style="padding-left: 5px">
              учесть переплату {{ overpayment }}</span
            >
          </div>
        </div>

        <div class="label">
          <label for="date">{{ $translate.t("formDate") }} </label>
        </div>
        <div class="input-div">
          <input
            v-model="form.date.value"
            type="datetime-local"
            name="date"
            class="ff-500-14 input"
          />
        </div>

        <div class="label">
          <label for="information">{{ $translate.t("info") }}: </label>
        </div>
        <div class="input-div">
          <textarea
            v-model="form.notes.value"
            name="information"
            id=""
            cols="30"
            rows="3"
            class="ff-500-14 input"
          ></textarea>
        </div>
        <div class="add-footer">
          <button @click="$emit('cancel')" class="btn-tbl ff-500-14">
            {{ $translate.t("cancel") }}
          </button>
          <button
            :disabled="!form.valid"
            @click="addPayment"
            class="btn-add ff-500-14"
          >
            {{
              isMassivePayment
                ? $translate.t("makeMassivePayment")
                : $translate.t("makePayment")
            }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped></style>
