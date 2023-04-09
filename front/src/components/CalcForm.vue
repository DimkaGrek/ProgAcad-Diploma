<script>
import { ref } from "vue";
import { useForm } from "../hooks/form";

const required = (val) => !!val;
const number = (val) => !isNaN(val);

export default {
  props: {
    counterId: {
      type: Number,
      default: 0,
    },
    lastCalc: {
      type: Object,
      defualt: null,
    },
    showUpdateCalc: {
      type: Boolean,
    },
    calcForUpdate: {
      type: Object,
      default: null,
    },
  },
  setup(props, { emit }) {
    const month = ref([
      "Январь",
      "Февраль",
      "Март",
      "Апрель",
      "Май",
      "Июнь",
      "Июль",
      "Август",
      "Сентябрь",
      "Октябрь",
      "Ноябрь",
      "Декабрь",
    ]);
    const year = ref([
      "2013",
      "2014",
      "2015",
      "2016",
      "2017",
      "2018",
      "2019",
      "2020",
      "2021",
      "2022",
      "2023",
    ]);
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

    const countNow = props.lastCalc ? props.lastCalc.countNow : null;
    const rate = props.lastCalc ? props.lastCalc.rate : null;

    // if (props.lastCalc !== null) {
    //   countNow.value = props.lastCalc.countNow;
    //   rate.value = props.lastCalc.rate;
    // }

    //console.log("month value: ", month.value[0]);
    //console.log("calcForUpdate: ", props.calcForUpdate.month);
    console.log("showUpdateCalc: ", props.showUpdateCalc);

    let monthIndexUpdate = -1;
    let yearUpdate = "";
    if (props.showUpdateCalc) {
      const currMonthYear = props.calcForUpdate.month.split(" ");
      monthIndexUpdate = month.value.indexOf(currMonthYear[0]);
      yearUpdate = currMonthYear[1];
    }

    // const greaterThanCountBefore = (val) =>
    //   Number(val) > Number(form.countBefore.value);

    const form = useForm({
      month: {
        value: props.showUpdateCalc
          ? month.value[monthIndexUpdate]
          : month.value[monthNow - 2],
        validators: { required },
      },
      year: {
        value: props.showUpdateCalc ? yearUpdate : yearNow,
        validators: { required },
      },
      countNow: {
        value: props.showUpdateCalc ? props.calcForUpdate.countNow : "",
        validators: { required, number },
      },
      countBefore: {
        value: props.showUpdateCalc
          ? props.calcForUpdate.countBefore
          : countNow,
        validators: { required, number },
      },
      rate: {
        value: props.showUpdateCalc ? props.calcForUpdate.rate : rate,
        validators: { required, number },
      },
      date: {
        value: props.showUpdateCalc ? props.calcForUpdate.date : dateString,
        validators: { required },
      },
      notes: {
        value: props.showUpdateCalc ? props.calcForUpdate.notes : "",
        validators: {},
      },
    });

    // отправляем данные на сервер
    async function sendFetch(uri, formSend) {
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
            console.log("КАЛЬКУЛЯЦИЯ СОХРАНЕНА!");
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

    async function addCalc() {
      props.showUpdateCalc
        ? console.log("РЕДАКТИРОВАНИЕ НАЧИСЛЕНИЯ!")
        : console.log("ДОБАВЛЕНИЕ НАЧИСЛЕНИЯ!");
      console.log(Object.keys(form));

      const formSend = Object.keys(form)
        .filter((k) => k !== "valid")
        .reduce((acc, key) => {
          acc[key] = form[key].value;
          return acc;
        }, {});
      formSend.date = props.showUpdateCalc
        ? formSend.date
        : formSend.date + ":00"; // add seconds to date
      console.log(JSON.stringify(formSend));

      // отправляем данные на сервер
      if (props.showUpdateCalc) {
        sendFetch(
          "api/calculations/update/" + props.calcForUpdate.id,
          formSend
        );
      } else {
        sendFetch("api/calculations/" + props.counterId, formSend);
      }
    }

    return {
      month,
      year,
      monthNow,
      yearNow,
      dateString,
      countNow,
      rate,
      props,
      form,
      hours,
      minutes,
      addCalc,
    };
  },
};
</script>

<template>
  <div class="add-new">
    <div class="add-header">
      <h1 class="ff-500-18">
        {{
          props.showUpdateCalc ? "Редагувати нарахування" : "Додати нарахування"
        }}
      </h1>
      <small>( * - обовя'зкові поля)</small>
    </div>
    <div class="add-main ff-500-14">
      <form @submit.prevent="">
        <div class="add-month-year">
          <div class="add-month">
            <div class="label">
              <label for="month"
                >За місяць <span class="indi-500">*</span></label
              >
            </div>
            <div class="input-div">
              <select
                v-model="form.month.value"
                @blur="form.month.blur"
                name="month"
                class="ff-500-14"
              >
                <option disabled value="">Виберіть місяць</option>
                <option
                  v-for="m in month"
                  :key="m"
                  :value="m"
                  :selected="m === form.month.value"
                  :class="{
                    invalid: !form.month.valid && form.month.touched,
                  }"
                >
                  {{ m }}
                </option>
              </select>
              <small v-if="form.month.touched && form.month.errors.required"
                >Будь-ласка виберіть місяць</small
              >
            </div>
          </div>
          <div class="add-year">
            <div class="label">
              <label for="year">рік <span class="indi-500">*</span></label>
            </div>
            <div class="input-div">
              <select
                v-model="form.year.value"
                name="year"
                class="ff-500-14 input"
              >
                <option disabled value="">Виберіть рік</option>
                <option
                  v-for="y in year"
                  :key="y"
                  :value="y"
                  :selected="y === form.year.value"
                >
                  {{ y }}
                </option>
              </select>
            </div>
          </div>
        </div>

        <div class="label">
          <label for="countnow"
            >Поточні показники <span class="indi-500">*</span></label
          >
        </div>
        <div class="input-div">
          <input
            v-model="form.countNow.value"
            @blur="form.countNow.blur"
            type="text"
            name="countnow"
            class="ff-500-14"
            :class="{
              input: form.countNow.valid || !form.countNow.touched,
              invalid: !form.countNow.valid && form.countNow.touched,
            }"
          />
          <small v-if="form.countNow.touched && form.countNow.errors.required"
            >Будь-ласка введіть Поточні показники</small
          >
          <small
            v-else-if="form.countNow.touched && form.countNow.errors.number"
            >Поточні показники повинені бути числом.</small
          >
        </div>

        <div class="label">
          <label for="countbefore"
            >Попередні показники <span class="indi-500">*</span></label
          >
        </div>
        <div class="input-div">
          <input
            v-model="form.countBefore.value"
            @blur="form.countBefore.blur"
            type="text"
            name="countbefore"
            class="ff-500-14"
            :class="{
              input: form.countBefore.valid || !form.countBefore.touched,
              invalid: !form.countBefore.valid && form.countBefore.touched,
            }"
          />
          <small
            v-if="form.countBefore.touched && form.countBefore.errors.required"
            >Будь-ласка введіть Попередні показники</small
          >
          <small
            v-else-if="
              form.countBefore.touched && form.countBefore.errors.number
            "
            >Попередні показники повинені бути числом.</small
          >
        </div>

        <div class="label">
          <label for="rate">Тариф <span class="indi-500">*</span></label>
        </div>
        <div class="input-div">
          <input
            v-model="form.rate.value"
            @blur="form.rate.blur"
            type="text"
            name="rate"
            class="ff-500-14"
            :class="{
              input: form.rate.valid || !form.rate.touched,
              invalid: !form.rate.valid && form.rate.touched,
            }"
          />
          <small v-if="form.rate.touched && form.rate.errors.required"
            >Будь-ласка введіть Тариф</small
          >
          <small v-else-if="form.rate.touched && form.rate.errors.number"
            >Тариф повинен бути числом.</small
          >
        </div>

        <div class="label"><label for="dateinput">Дата </label></div>
        <div class="input-div">
          <input
            v-model="form.date.value"
            type="datetime-local"
            name="dateinput"
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
            @click="addCalc"
            class="btn-add ff-500-14"
          >
            {{
              props.showUpdateCalc
                ? "Редагувати нарахування"
                : "Додати нарахування"
            }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped></style>
