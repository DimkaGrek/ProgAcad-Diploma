<script>
import { useForm } from "../hooks/form";
import { computed } from "vue";

const required = (val) => !!val;
const number = (val) => !isNaN(val);

export default {
  props: ["calcId", "amount", "counterId", "selectedCalcsForPayment"],
  setup(props, { emit }) {
    console.log("props.calcId: ", props.calcId);
    console.log("props.amount: ", props.amount);
    console.log("props.counterId: ", props.counterId);

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
        console.log("ДОБАВЛЕНИЕ МАССОВОГО НАЧИСЛЕНИЯ!");
      } else {
        console.log("ДОБАВЛЕНИЕ НАЧИСЛЕНИЯ!");
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
        formSend.amountsPay = props.amount;
        sendFetch("api/payments/" + props.calcId, formSend);
      }
      console.log(JSON.stringify(formSend));
    }

    // if (calcForPayment.value.length === 0)
    //   console.log("calcForPayment пусто!!!");
    // else console.log("calcForPayment: ", calcForPayment);

    return {
      form,
      addPayment,
      isMassivePayment,
      monthesPay,
      idsPay,
      amountsPay,
    };
  },
};
</script>

<template>
  <div class="add-new">
    <div class="add-header">
      <h1 v-if="isMassivePayment" class="ff-500-18">Здійснити масову оплату</h1>
      <h1 v-else class="ff-500-18">Здійснити оплату</h1>
      <small>( * - обовя'зкові поля)</small>
    </div>
    <div class="add-main ff-500-14">
      <form @submit.prevent="">
        <div class="label">
          <label for="receiptId"
            >Номер чека про оплату <span class="indi-500">*</span></label
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
          <small v-if="form.receiptId.touched && form.receiptId.errors.required"
            >Будь-ласка введіть Номер чека про оплату</small
          >
          <small
            v-else-if="form.receiptId.touched && form.receiptId.errors.number"
            >Номер чека про оплату повинен бути числом.</small
          >
        </div>

        <div class="label">
          <label for="amount"
            >Сума оплати <span class="indi-500">*</span></label
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
          <small v-if="form.amount.touched && form.amount.errors.required"
            >Будь-ласка введіть Суму оплати</small
          >
          <small v-else-if="form.amount.touched && form.amount.errors.number"
            >Сума оплати повинна бути числом.</small
          >
        </div>

        <div class="label"><label for="date">Дата </label></div>
        <div class="input-div">
          <input
            v-model="form.date.value"
            type="datetime-local"
            name="date"
            class="ff-500-14 input"
          />
        </div>

        <div class="label">
          <label for="information">Додаткова інформація: </label>
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
            Скасувати
          </button>
          <button
            :disabled="!form.valid"
            @click="addPayment"
            class="btn-add ff-500-14"
          >
            {{
              isMassivePayment ? "Здійснити масову оплату" : "Здійснити оплату"
            }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped></style>
