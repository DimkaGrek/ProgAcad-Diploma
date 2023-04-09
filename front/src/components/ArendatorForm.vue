<script>
import { useForm } from "../hooks/form";
// import { ref } from "vue";

const required = (val) => !!val;

export default {
  props: ["selectedArendatorData", "showUpdateArendator"],
  emits: ["submitForm", "cancel"],
  setup(props, { emit }) {
    const form = useForm({
      surname: {
        value: props.showUpdateArendator
          ? props.selectedArendatorData.surname
          : "",
        validators: { required },
      },
      name: {
        value: props.showUpdateArendator
          ? props.selectedArendatorData.name
          : "",
        validators: { required },
      },
      evidence: {
        value: props.showUpdateArendator
          ? props.selectedArendatorData.evidence
          : "",
        validators: { required },
      },
      passport: {
        value: props.showUpdateArendator
          ? props.selectedArendatorData.passport
          : "",
        validators: {},
      },
      address: {
        value: props.showUpdateArendator
          ? props.selectedArendatorData.address
          : "",
        validators: {},
      },
      typeCompany: {
        value: props.showUpdateArendator
          ? props.selectedArendatorData.typeCompany
          : "",
        validators: { required },
      },
      phone1: {
        value: props.showUpdateArendator
          ? props.selectedArendatorData.phone1
          : "",
        validators: { required },
      },
      phone2: {
        value: props.showUpdateArendator
          ? props.selectedArendatorData.phone2
          : "",
        validators: {},
      },
      email: {
        value: props.showUpdateArendator
          ? props.selectedArendatorData.email
          : "",
        validators: {},
      },
      notes: {
        value: props.showUpdateArendator
          ? props.selectedArendatorData.notes
          : "",
        validators: {},
      },
    });

    async function addArendator() {
      props.showUpdateArendator
        ? console.log("РЕДАКТИРОВАНИЕ АРЕНДАТОРА")
        : console.log("ДОБАВЛЕНИЕ АРЕНДАТОРА");
      console.log(Object.keys(form));

      const formSend = Object.keys(form)
        .filter((k) => k !== "valid")
        .reduce((acc, key) => {
          acc[key] = form[key].value;
          return acc;
        }, {});
      console.log(JSON.stringify(formSend));
      const uri = props.showUpdateArendator
        ? "api/arendators/update/" + props.selectedArendatorData.id
        : "api/arendators";
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
            console.log("АРЕНДАТОР СОХРАНЕН");
            alert("Данні збережені успішно!");
            emit("submitForm");
          } else {
            // Обработка ошибки
            response.text().then((text) => {
              console.error("Ошибка " + response.status + " - " + text);
            });
          }
        })
        .catch((error) => console.error("Ошибка сети: " + error));
    }

    return { form, addArendator };
  },
};
</script>

<template>
  <div class="add-new">
    <div class="add-header">
      <h1 class="ff-500-18">
        {{
          showUpdateArendator
            ? "Редагувати арендатора"
            : "Додати нового арендатора"
        }}
      </h1>
      <small>( * - обовя'зкові поля)</small>
      <!-- showUpdateArendator: {{ props.showUpdateArendator }} -->
    </div>
    <div class="add-main ff-500-14">
      <form @submit.prevent="">
        <div class="label">
          <label for="name">Им'я <span class="indi-500">*</span></label>
        </div>
        <div class="input-div">
          <input
            v-model="form.name.value"
            @blur="form.name.blur"
            type="text"
            name="name"
            class="ff-500-14"
            :class="{
              input: form.name.valid || !form.name.touched,
              invalid: !form.name.valid && form.name.touched,
            }"
          />
          <small v-if="form.name.touched && form.name.errors.required"
            >Будь-ласка введіть Им'я</small
          >
        </div>

        <div class="label">
          <label for="surname">Фамілія <span class="indi-500">*</span></label>
        </div>
        <div class="input-div">
          <input
            v-model="form.surname.value"
            @blur="form.surname.blur"
            type="text"
            name="surname"
            class="ff-500-14"
            :class="{
              input: form.surname.valid || !form.surname.touched,
              invalid: !form.surname.valid && form.surname.touched,
            }"
          />
          <small v-if="form.surname.touched && form.surname.errors.required"
            >Будь-ласка введіть Фамілію</small
          >
        </div>

        <div class="label">
          <label for="evidence"
            >Свідотство <span class="indi-500">*</span></label
          >
        </div>
        <div class="input-div">
          <input
            v-model="form.evidence.value"
            @blur="form.evidence.blur"
            type="text"
            name="evidence"
            class="ff-500-14"
            :class="{
              input: form.evidence.valid || !form.evidence.touched,
              invalid: !form.evidence.valid && form.evidence.touched,
            }"
          />
          <small v-if="form.evidence.touched && form.evidence.errors.required"
            >Будь-ласка введіть Свідотство</small
          >
        </div>

        <div class="label">
          <label for="passport">Паспорт <span class="indi-500">*</span></label>
        </div>
        <div class="input-div">
          <input
            v-model="form.passport.value"
            @blur="form.passport.blur"
            type="text"
            name="passport"
            class="ff-500-14"
            :class="{
              input: form.passport.valid || !form.passport.touched,
              invalid: !form.passport.valid && form.passport.touched,
            }"
          />
          <small v-if="form.passport.touched && form.passport.errors.required"
            >Будь-ласка введіть Паспорт</small
          >
        </div>

        <div class="label">
          <label for="address">Адреса <span class="indi-500">*</span></label>
        </div>
        <div class="input-div">
          <input
            v-model="form.address.value"
            @blur="form.address.blur"
            type="text"
            name="address"
            class="ff-500-14"
            :class="{
              input: form.address.valid || !form.address.touched,
              invalid: !form.address.valid && form.address.touched,
            }"
          />
          <small v-if="form.address.touched && form.address.errors.required"
            >Будь-ласка введіть Адресу</small
          >
        </div>

        <div class="label">
          <label for="typeCompany"
            >Форма компаніі (Юр. або ФОП) <span class="indi-500">*</span></label
          >
        </div>
        <div class="input-div">
          <input
            v-model="form.typeCompany.value"
            @blur="form.typeCompany.blur"
            type="text"
            name="typeCompany"
            class="ff-500-14"
            :class="{
              input: form.typeCompany.valid || !form.typeCompany.touched,
              invalid: !form.typeCompany.valid && form.typeCompany.touched,
            }"
          />
          <small
            v-if="form.typeCompany.touched && form.typeCompany.errors.required"
            >Будь-ласка введіть Форму компаніі</small
          >
        </div>

        <div class="label">
          <label for="phone1">Телефон 1 <span class="indi-500">*</span></label>
        </div>
        <div class="input-div">
          <input
            v-model="form.phone1.value"
            @blur="form.phone1.blur"
            type="text"
            name="phone1"
            class="ff-500-14"
            :class="{
              input: form.phone1.valid || !form.phone1.touched,
              invalid: !form.phone1.valid && form.phone1.touched,
            }"
          />
          <small v-if="form.phone1.touched && form.phone1.errors.required"
            >Будь-ласка введіть Телефон 1</small
          >
        </div>

        <div class="label">
          <label for="phone2">Телефон 2 </label>
        </div>
        <div class="input-div">
          <input
            v-model="form.phone2.value"
            @blur="form.phone2.blur"
            type="text"
            name="phone2"
            class="ff-500-14 input"
          />
        </div>

        <div class="label">
          <label for="email">E-mail <span class="indi-500">*</span></label>
        </div>
        <div class="input-div">
          <input
            v-model="form.email.value"
            @blur="form.email.blur"
            type="text"
            name="email"
            class="ff-500-14 input"
          />
        </div>

        <div class="label">
          <label for="information">Додаткова інформація </label>
        </div>
        <div class="input-div">
          <textarea
            v-model="form.notes.value"
            name="information"
            id=""
            cols="30"
            rows="3"
            class="ff-500-14"
          ></textarea>
        </div>
        <div class="add-footer">
          <button @click="$emit('cancel')" class="btn-tbl ff-500-14">
            Скасувати
          </button>
          <button
            @click="addArendator"
            :disabled="!form.valid"
            class="btn-add ff-500-14"
          >
            {{
              showUpdateArendator
                ? "Редагувати арендатора"
                : "Додати арендатора"
            }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped></style>
