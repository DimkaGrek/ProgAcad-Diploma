<script>
import { useForm } from "../hooks/form";
import { reactive, defineComponent, ref, onMounted, watch, inject } from "vue";

const required = (val) => !!val;
const minLength = (num) => (val) => val.length >= num;
const number = (val) => !isNaN(val);

export default defineComponent({
  props: ["selectedCounterData", "showUpdateCounter"],
  setup(props, { emit }) {
    // console.log(
    //   "selectedCounterData in CounterForm: ",
    //   props.selectedCounterData.pavilion
    // );
    console.log("showUpdateCounter: ", props.showUpdateCounter);
    const state = reactive({
      submit: false,
      existCounter: false,
    });

    const clearSearch = inject("clearSearch");

    const arendators = ref([]);
    const arendator = ref(null);
    const isArendatorsLoaded = ref(false);

    const fetchArendators = async () => {
      const response = await fetch("api/arendators");
      const data = await response.json();

      arendators.value = data.sort((arend1, arend2) =>
        arend1.surname?.localeCompare(arend2.surname)
      );
      if (props.showUpdateCounter) {
        arendator.value = arendators.value.find(
          (arendator) =>
            arendator.surname == props.selectedCounterData.surname &&
            arendator.name == props.selectedCounterData.name
        );
      }
      isArendatorsLoaded.value = true;
    };
    console.log("arendator: ", arendator.value);

    onMounted(() => {
      fetchArendators();
    });

    const now = new Date();
    const year = now.getFullYear();
    const month = now.getMonth() + 1;
    const day = now.getDate();
    const dateString = `${year}-${month.toString().padStart(2, "0")}-${day
      .toString()
      .padStart(2, "0")}`;

    const form = useForm({
      pavilion: {
        value: props.showUpdateCounter
          ? props.selectedCounterData.pavilion
          : "",
        validators: { required },
      },
      place: {
        value: props.showUpdateCounter ? props.selectedCounterData.place : "",
        validators: { required },
      },
      type: {
        value: props.showUpdateCounter ? props.selectedCounterData.type : "",
        validators: { required },
      },
      number: {
        value: props.showUpdateCounter
          ? props.selectedCounterData.number.toString()
          : "",
        validators: { required, minLength: minLength(5), number },
      },
      capacity: {
        value: props.showUpdateCounter
          ? props.selectedCounterData.capacity
          : "",
        validators: { required, number },
      },
      dateManufac: {
        value: props.showUpdateCounter
          ? props.selectedCounterData.dateManufac
          : dateString,
        validators: {},
      },
      dateInstall: {
        value: props.showUpdateCounter
          ? props.selectedCounterData.dateInstall
          : dateString,
        validators: {},
      },
      arendator: {
        value: "",
        validators: { required },
      },
      parent: {
        value: props.showUpdateCounter ? props.selectedCounterData.parent : "",
        validators: { number },
      },
      notes: {
        value: props.showUpdateCounter ? props.selectedCounterData.notes : "",
        validators: {},
      },
    });

    watch(isArendatorsLoaded, (newValue) => {
      if (newValue) {
        arendator.value
          ? (form.arendator.value = arendator.value.id)
          : (form.arendator.value = "");
      }
    });

    async function addCounter() {
      props.showUpdateCounter
        ? console.log("РЕДАКТИРОВАНИЕ СЧЕТЧИКА")
        : console.log("ДОБАВЛЕНИЕ СЧЕТЧИКА");
      console.log(Object.keys(form));

      const formSend = Object.keys(form)
        .filter((k) => k !== "valid")
        .reduce((acc, key) => {
          acc[key] = form[key].value;
          return acc;
        }, {});
      console.log(JSON.stringify(formSend));
      const uri = props.showUpdateCounter
        ? "api/counters/update/" + props.selectedCounterData.id
        : "api/counters";
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
            console.log("СЧЕТЧИК СОХРАНЕН");
            alert("Данні збережені успішно!");
            clearSearch();
            emit("submitForm");
          } else {
            // Обработка ошибки
            response.text().then((text) => {
              console.error("Ошибка " + response.status + " - " + text);
            });
            if (response.status == 409) {
              state.existCounter = true;
              form.number.valid = false;
            }
          }
        })
        .catch((error) => console.error("Ошибка сети: " + error));
    }

    function resetExistCounter() {
      state.existCounter = false;
    }

    return {
      form,
      addCounter,
      state,
      resetExistCounter,
      arendators,
      arendator,
      isArendatorsLoaded,
      clearSearch,
    };
  },
});
</script>

<template>
  <div class="add-new">
    <div class="add-header">
      <h1 class="ff-500-18">
        {{
          showUpdateCounter
            ? $translate.t("formHeadCounterEdit")
            : $translate.t("formHeadCounterAdd")
        }}
      </h1>
      <small>( * - {{ $translate.t("requiredFields") }})</small>
      <!-- showUpdateCounter: {{ props.showUpdateCounter }} -->
    </div>
    <div class="add-main ff-500-14">
      <form @submit.prevent="">
        <div class="label">
          <label for="pavilion"
            >{{ $translate.t("formPavilion") }}
            <span class="indi-500">*</span></label
          >
        </div>
        <div class="input-div">
          <input
            v-model="form.pavilion.value"
            @blur="form.pavilion.blur"
            type="text"
            name="pavilion"
            class="ff-500-14"
            :class="{
              input: form.pavilion.valid || !form.pavilion.touched,
              invalid: !form.pavilion.valid && form.pavilion.touched,
            }"
          />
          <small
            v-if="form.pavilion.touched && form.pavilion.errors.required"
            >{{ $translate.t("formErrorPavilionReq") }}</small
          >
        </div>

        <div class="label">
          <label for="place"
            >{{ $translate.t("formPlace") }}
            <span class="indi-500">*</span></label
          >
        </div>
        <div class="input-div">
          <input
            v-model="form.place.value"
            @blur="form.place.blur"
            type="text"
            name="place"
            class="ff-500-14"
            :class="{
              input: form.place.valid || !form.place.touched,
              invalid: !form.place.valid && form.place.touched,
            }"
          />
          <small v-if="form.place.touched && form.place.errors.required">{{
            $translate.t("formErrorPlaceReq")
          }}</small>
        </div>

        <div class="label">
          <label for="type"
            >{{ $translate.t("formTypeCounter") }}
            <span class="indi-500">*</span></label
          >
        </div>
        <div class="input-div">
          <input
            v-model="form.type.value"
            @blur="form.type.blur"
            type="text"
            name="type"
            class="ff-500-14"
            :class="{
              input: form.type.valid || !form.type.touched,
              invalid: !form.type.valid && form.type.touched,
            }"
          />
          <small v-if="form.type.touched && form.type.errors.required">{{
            $translate.t("formErrorTypeReq")
          }}</small>
        </div>

        <div class="label">
          <label for="number"
            >{{ $translate.t("formCounter") }}
            <span class="indi-500">*</span></label
          >
        </div>
        <div class="input-div">
          <input
            v-model="form.number.value"
            @blur="form.number.blur"
            @input="resetExistCounter"
            type="text"
            name="number"
            class="ff-500-14"
            :class="{
              input: form.number.valid || !form.number.touched,
              invalid: !form.number.valid && form.number.touched,
            }"
          />
          <small v-if="form.number.touched && form.number.errors.required">{{
            $translate.t("formErrorCounterNumReq")
          }}</small>
          <small v-else-if="form.number.touched && form.number.errors.minLength"
            >{{ $translate.t("formErrorCounterMinLength") }}
            {{ form.number.value.length }}.</small
          >
          <small v-else-if="form.number.touched && form.number.errors.number"
            >{{ $translate.t("formErrorCounterDigital") }}.</small
          >
          <small v-else-if="form.number.touched && state.existCounter"
            >{{ $translate.t("formErrorCounterExist") }}.</small
          >
        </div>

        <div class="label">
          <label for="capacity"
            >{{ $translate.t("formCapacityCounter") }}
            <span class="indi-500">*</span></label
          >
        </div>
        <div class="input-div">
          <input
            v-model="form.capacity.value"
            @blur="form.capacity.blur"
            type="text"
            name="capacity"
            class="ff-500-14"
            :class="{
              input: form.capacity.valid || !form.capacity.touched,
              invalid: !form.capacity.valid && form.capacity.touched,
            }"
          />
          <small
            v-if="form.capacity.touched && form.capacity.errors.required"
            >{{ $translate.t("formErrorCapacityReq") }}</small
          >
          <small
            v-else-if="form.capacity.touched && form.capacity.errors.number"
            >{{ $translate.t("formErrorCapacityDigital") }}.</small
          >
        </div>

        <div class="label">
          <label for="build_date"
            >{{ $translate.t("formDateManufac") }}
            <span class="indi-500">*</span></label
          >
        </div>
        <div class="input-div">
          <input
            v-model="form.dateManufac.value"
            type="date"
            name="build_date"
            class="ff-500-14 input"
          />
        </div>

        <div class="label">
          <label for="install_date"
            >{{ $translate.t("formDateInstall") }}
            <span class="indi-500">*</span></label
          >
        </div>
        <div class="input-div">
          <input
            v-model="form.dateInstall.value"
            type="date"
            name="install_date"
            class="ff-500-14 input"
          />
        </div>

        <div class="label">
          <label for="arendator"
            >{{ $translate.t("formTenant") }}
            <span class="indi-500">*</span></label
          >
        </div>
        <div class="input-div">
          <select
            v-model="form.arendator.value"
            @blur="form.arendator.blur"
            name="arendator"
            id=""
            class="ff-500-14"
            :class="{
              invalid: !form.arendator.valid && form.arendator.touched,
            }"
          >
            <option disabled selected value="">
              {{ $translate.t("formSelectTenant") }}
            </option>
            <option
              v-for="a in arendators"
              :key="a.id"
              :value="a.id"
              :selected="a.id === form.arendator.value"
            >
              {{ a.surname }} {{ a.name }}
            </option>
          </select>
          <small
            v-if="form.arendator.touched && form.arendator.errors.required"
            >{{ $translate.t("formErrorTenantReq") }}</small
          >
        </div>

        <div class="label">
          <label for="parent">{{ $translate.t("formParentCounter") }}: </label>
        </div>
        <div class="input-div">
          <input
            v-model="form.parent.value"
            @blur="form.parent.blur"
            type="text"
            name="install_date"
            class="ff-500-14"
            :class="{
              input: form.parent.valid || !form.parent.touched,
              invalid: !form.parent.valid && form.parent.touched,
            }"
          />
          <small v-if="form.parent.touched && form.parent.errors.number"
            >{{ $translate.t("formErrorParentCounterDigit") }}.</small
          >
        </div>

        <div class="label">
          <label for="information">{{ $translate.t("info") }} </label>
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
            {{ $translate.t("cancel") }}
          </button>
          <button
            @click="addCounter"
            :disabled="!form.valid"
            class="btn-add ff-500-14"
          >
            {{
              showUpdateCounter
                ? $translate.t("formHeadCounterEdit")
                : $translate.t("formHeadCounterAdd")
            }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped></style>
