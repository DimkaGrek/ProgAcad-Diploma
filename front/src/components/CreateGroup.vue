<script>
import { useForm } from "../hooks/form";

const required = (val) => !!val;

export default {
  emits: ["submitForm"],
  setup(_, { emit }) {
    const form = useForm({
      name: {
        value: "",
        validators: { required },
      },
      description: {
        value: "",
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
            console.log("Группа СОЗДАНА!");
            alert("Данні збережені успішно!");
            emit("submitForm");
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

    function createGroupName() {
      const formSend = Object.keys(form)
        .filter((k) => k !== "valid")
        .reduce((acc, key) => {
          acc[key] = form[key].value;
          return acc;
        }, {});
      sendFetch("api/groups", formSend);
      console.log(JSON.stringify(formSend));
    }

    return { form, createGroupName };
  },
};
</script>

<template>
  <div class="add-new">
    <div class="add-header">
      <h1 class="ff-500-18">
        {{ $translate.t("addNewGroup") }}
      </h1>
    </div>
    <div class="add-main ff-500-14">
      <form @submit.prevent="">
        <div class="label">
          <label for="addgroup"
            >{{ $translate.t("formGroupName") }}
            <span class="indi-500">*</span></label
          >
        </div>
        <div class="input-div">
          <input
            v-model="form.name.value"
            @blur="form.name.blur"
            type="text"
            name="addgroup"
            class="ff-500-14"
            :class="{
              input: form.name.valid || !form.name.touched,
              invalid: !form.name.valid && form.name.touched,
            }"
          />
          <small v-if="form.name.touched && form.name.errors.required">{{
            $translate.t("formErrorGroupName")
          }}</small>
        </div>

        <div class="label">
          <label for="information">{{ $translate.t("info") }}: </label>
        </div>
        <div class="input-div">
          <textarea
            v-model="form.description.value"
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
            @click="createGroupName"
            class="btn-add ff-500-14"
          >
            {{ $translate.t("addNewGroup") }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped></style>
