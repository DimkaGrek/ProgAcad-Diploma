<script>
import { ref, onMounted, watch, inject } from "vue";
import DocView from "./components/DocView.vue";
import TopNav from "./components/TopNav.vue";
import CountersTable from "./components/CountersTable.vue";
import CalculationTable from "./components/CalculationTable.vue";
import CounterForm from "./components/CounterForm.vue";
import PaymentTable from "./components/PaymentTable.vue";
import CalcForm from "./components/CalcForm.vue";
import PaymentForm from "./components/PaymentForm.vue";
import ArendatorsTable from "./components/ArendatorsTable.vue";
import ArendatorForm from "./components/ArendatorForm.vue";

export default {
  components: {
    DocView,
    TopNav,
    CountersTable,
    CalculationTable,
    CounterForm,
    PaymentTable,
    CalcForm,
    PaymentForm,
    ArendatorsTable,
    ArendatorForm,
  },
  setup() {
    const username = ref(null);
    const globalState = inject("$globalState");
    const loading = ref(true);

    const show = ref(false);
    const showHome = ref(false);
    const showCounters = ref(false);
    const showCalc = ref(false);
    const showPayTable = ref(false);
    const showCalcForm = ref(false);
    const showPayForm = ref(false);
    const showUpdateCalc = ref(false);
    const showMessage = ref(false);
    const showUpdateCounter = ref(false);
    const showArendators = ref(false);
    const showArendForm = ref(false);
    const showUpdateArendator = ref(false);

    const messageTop = ref(0);
    const messageLeft = ref(0);
    const messageText = ref("");

    const counters = ref([]);
    const totalCounters = ref();
    const deleteCounters = ref(null);
    const deletedCounters = ref([]);
    const numberOfDeletedCounters = ref(0);

    const arendators = ref([]);
    const totalArendators = ref();
    const selectedArendatorForUpdate = ref();

    const calcRecords = ref([]);
    const totalCalcRecords = ref();
    const totalCalcAmount = ref(0);
    const totalPayAmount = ref(0);
    const selectedCalc = ref(null);
    const payRecords = ref([]);
    const calcMonth = ref("");
    const lastCalc = ref(); // последняя квитанция по счетчику
    const calcForUpdate = ref(); // квитанция для редактирования
    const selectedAmount = ref(); // выбранная сумма для оплаты
    const selectedCounterData = ref(); // данные по выбранному счетчику
    const isCheckedForPayment = ref(false); // блокирует кнопку массовой оплаты квитанций
    const selectedCalcsForPayment = ref([]); // для выбраных квитанций для оплаты

    const searchQuery = ref("");
    const clearSearch = () => {
      searchQuery.value = "";
    };
    const selectedCounter = ref(null);
    const selectedCounterForUpdate = ref();

    const handleSearchChange = (value) => {
      searchQuery.value = value;
    };

    const MassivePayment = ref(null);
    const PrintCalculations = ref(null);
    const deleteCalculations = ref(null);

    function callDelete() {
      console.log("callDelete вызвана ...");
      if (showCounters.value) {
        callDeleteCountersFromParent();
      }
      if (showCalc.value) {
        callDeleteCalculationsFromParent();
      }
    }

    const onRegisterMassivePayment = (fn) => {
      console.log("вызов onRegisterMassivePayment");
      MassivePayment.value = fn;
    };

    const callMassivePaymentFromParent = () => {
      console.log("callMassivePaymentFromParent вызвана");
      if (isCheckedForPayment.value)
        console.log("isCheckedForPayment: ", isCheckedForPayment.value);
      if (MassivePayment.value) {
        MassivePayment.value();
      } else {
        console.log("Функция дочернего компонента не доступна");
      }
    };

    const onRegisterPrintCalculations = (fn) => {
      console.log("вызов onRegisterPrintCalculations");
      PrintCalculations.value = fn;
    };

    const callPrintCalculationsFromParent = () => {
      console.log("callPrintCalculationsFromParent вызвана");
      if (PrintCalculations.value) {
        PrintCalculations.value();
      } else {
        console.log("Функция печати дочернего компонента не доступна");
      }
    };

    const onRegisterDeleteCalculations = (fn) => {
      console.log("вызов onRegisterPrintCalculations");
      deleteCalculations.value = fn;
    };

    const callDeleteCalculationsFromParent = () => {
      console.log("callDeleteCalculationsFromParent вызвана");
      if (deleteCalculations.value) {
        deleteCalculations.value();
      } else {
        console.log("Функция печати дочернего компонента не доступна");
      }
    };

    async function selectDeleteCalculations(calcIds, counterId) {
      console.log("selectDeleteCalculations ...");
      console.log("calcIds: ", calcIds);
      console.log("counterId: ", counterId);
      await fetch("api/calculations/delete/" + calcIds, {
        method: "POST",
        // headers: {
        //   "Content-Type": "application/json",
        // },
        // body: JSON.stringify(),
      })
        .then((response) => {
          if (response.ok) {
            //response.json()
            console.log("КАЛЬКУЛЯЦИИ УДАЛЕНЫ!");
            alert("Калькуляції успішно видалені!");
            fetchCalcs(counterId);
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

    // когда выбрали оплатить несколько квитанций
    const selectMassiveForPayment = (selectedIds, amount) => {
      selectedAmount.value = amount;
      selectedCalcsForPayment.value = calcRecords.value.filter((record) =>
        selectedIds.includes(record.id)
      );
      showPayForm.value = true;
      show.value = true;
      //console.log("selectedCalcsForPayment.value: ", selectedCalcsForPayment.value[0].id);
    };

    function clickCounters() {
      console.log("Нажали на кнопку Counters");
      showHome.value = false;
      showCounters.value = true;
      showCalc.value = false;
      showArendators.value = false;
    }

    function clickHome() {
      console.log("Нажали на кнопку Home");
      showHome.value = true;
      showCounters.value = false;
      showCalc.value = false;
      showArendators.value = false;
    }

    function clickArendators() {
      console.log("Нажали на кнопку Arendators");
      if (arendators.value.length === 0) {
        loading.value = true;
        fetchArendators();
      }
      showArendators.value = true;
      showHome.value = false;
      showCounters.value = false;
      showCalc.value = false;
    }

    // функция для перезагрузки счетчиков, когда был добавлен новый счетчик
    function reloadCounters() {
      show.value = false;
      fetchCounters();
    }

    function reloadArendators() {
      show.value = false;
      fetchArendators();
    }

    function reloadCalculation(counterId) {
      show.value = false;
      selectedCalcsForPayment.value = [];
      fetchCalcs(counterId);
    }

    const selectCounter = (counterId, counterData) => {
      selectedCounter.value = counterId;
      selectedCounterData.value = counterData;
      calcRecords.value = [];
      console.log("selectedCounter: ", selectedCounter.value);
      loading.value = true;
      fetchCalcs(counterId);
      showCounters.value = false;
      showCalc.value = true;
    };

    // Функция для загрузки счетчика с сервера
    async function fetchCounter(uri, counterId) {
      try {
        const response = await fetch(uri + counterId);
        const data = await response.json();
        selectedCounterForUpdate.value = data;
      } catch (error) {
        console.error("Ошибка загрузки данных:", error);
      }
    }

    const selectUpdateCounter = (counterId) => {
      console.log("Выбрана selectUpdateCounter in App.vue... ");
      (async () => {
        await fetchCounter("api/counters/", counterId);
        console.log("selectedCounterData: ", selectedCounterForUpdate.value);
        showUpdateCounter.value = true;
        show.value = true;
      })();
    };

    const onRegisterDeleteCounters = (fn) => {
      console.log("вызов onRegisterDeleteCounters");
      deleteCounters.value = fn;
    };

    const callDeleteCountersFromParent = () => {
      console.log("callDeleteCountersFromParent вызвана");
      if (deleteCounters.value) {
        deleteCounters.value();
      } else {
        console.log("Функция печати дочернего компонента не доступна");
      }
    };

    const selectDeleteCounter = async (counterIds) => {
      console.log("Выбрана selectDeleteCounter in App.vue... ");
      console.log("counterIds: ", counterIds);
      await fetch("api/counters/delete/" + counterIds, {
        method: "POST",
      })
        .then((response) => {
          if (response.ok) {
            console.log("Счетчик(и) удален(ы)!");
            counterIds.length > 1
              ? alert("Лічильники успішно видалені!")
              : alert("Лічильник успішно видален!");
            fetchCounters();
            fetchNumberOfDeletedCounters();
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
      // async () => {
      //   await fetchDeleteCounters("api/counters/delete/", counterIds);
      // };
    };

    const selectPayForm = (calcId, amount, counterId) => {
      selectedCalc.value = calcId;
      selectedAmount.value = amount;
      selectedCounter.value = counterId;
      showPayForm.value = true;
      show.value = true;
    };

    // когда нажали на кнопку добавления нового начисления
    const selectCalcForm = (counterId) => {
      selectedCounter.value = counterId;
      showCalcForm.value = true;
      show.value = true;
    };

    const selectCalc = (calcId, month) => {
      console.log("Выбрали квитанцию: ", calcId);
      selectedCalc.value = calcId;
      calcMonth.value = month;
      fetchPay(calcId);
      console.log("calcMonth: ", calcMonth.value);
      show.value = true;
      showPayTable.value = true;
    };

    const selectUpdateCalc = (calcId) => {
      console.log("const selectUpdateCalc...");
      selectedCalc.value = calcId;
      calcForUpdate.value = calcRecords.value.filter(
        (record) => calcId === record.id
      )[0];
      console.log("calcForUpdate (App) : ", calcForUpdate.value);
      showCalcForm.value = true;
      showUpdateCalc.value = true;
      show.value = true;
    };

    // когда нажали кнопку добавить арендатора
    function selectArendForm() {
      showArendForm.value = true;
      show.value = true;
    }

    const selectUpdateArendator = async (arendatorId) => {
      console.log("const selectUpdateArendator...");
      try {
        const response = await fetch("api/arendators/" + arendatorId);
        const data = await response.json();
        selectedArendatorForUpdate.value = data;
        showArendForm.value = true;
        showUpdateArendator.value = true;
        show.value = true;
      } catch (error) {
        console.error("Ошибка загрузки данных:", error);
      }
    };

    async function fetchPay(calcId) {
      try {
        const response = await fetch("api/payments/" + calcId);
        const data = await response.json();
        payRecords.value = data;
        console.log(payRecords.value);
      } catch (error) {
        console.error("Ошибка загрузки данных:", error);
      }
    }

    async function fetchCalcs(counterId) {
      try {
        const response = await fetch("api/calculation/" + counterId);
        const data = await response.json();
        calcRecords.value = data;
        //console.log("Calculation Records: ", calcRecords.value);
        totalCalcRecords.value = calcRecords.value.length;
        lastCalc.value = calcRecords.value[calcRecords.value.length - 1];
        // console.log(
        //   "Object keys of calcRecords: ",
        //   calcRecords.value[0].amount
        // );
        // получаем общую сумму начислений
        totalCalcAmount.value = Object.keys(calcRecords.value).reduce(
          (acc, k) => acc + calcRecords.value[k].amount,
          0
        );
        console.log("totalCalcAmount", totalCalcAmount.value);
        // получаем общую сумму оплат по счетчику
        totalPayAmount.value = await fetch(
          "api/payments/sum/" + counterId
        ).then((response) => response.text());
        console.log("totalPayAmount", totalPayAmount.value);
        loading.value = false;
      } catch (error) {
        console.error("Ошибка загрузки данных:", error);
      }
    }

    // Функция для загрузки счетчиков с сервера
    async function fetchCounters() {
      try {
        const response = await fetch("api/counters");
        const data = await response.json();
        counters.value = data;
        console.log(counters.value);
        totalCounters.value = counters.value.length;
        loading.value = false;
      } catch (error) {
        console.error("Ошибка загрузки данных:", error);
      }
    }

    // Функция для загрузки арендаторов с сервера
    async function fetchArendators() {
      try {
        const response = await fetch("api/arendators");
        const data = await response.json();
        arendators.value = data;
        console.log(arendators.value);
        totalArendators.value = arendators.value.length;
        loading.value = false;
      } catch (error) {
        console.error("Ошибка загрузки данных:", error);
      }
    }

    async function fetchCurrentUser() {
      try {
        const response = await fetch("api/current", {
          //credentials: "include", // передаем куки для авторизации
        });
        const userData = await response.json();
        username.value = userData.username;
        globalState.setUsername(username.value);
        fetchCounters();
        showCounters.value = true;
      } catch (error) {
        console.error(error);
      }
    }

    async function fetchNumberOfDeletedCounters() {
      try {
        const response = await fetch("api/counters/delCount", {
          //credentials: "include", // передаем куки для авторизации
        });
        const data = await response.json();
        numberOfDeletedCounters.value = data;
      } catch (error) {
        console.error(error);
      }
    }

    onMounted(fetchCurrentUser(), fetchNumberOfDeletedCounters());

    watch(show, (newValue) => {
      if (newValue) {
        // Выполнить действия, когда showDialog становится true
        console.log("Модальное окно открыто");
      } else {
        // Выполнить действия, когда showDialog становится false
        console.log("Модальное окно закрыто");
        showPayTable.value = false;
        showCalcForm.value = false;
        showPayForm.value = false;
        showUpdateCalc.value = false;
        showUpdateCounter.value = false;
        showArendForm.value = false;
      }
    });

    // метод для разлогинивания пользователя
    const logout = async () => {
      const response = await fetch("/api/logout");
      if (response.ok) {
        console.log("response status: ", response.status);
        location.reload();
        //fetchCurrentUser();
        //username.value = null;
      }
      console.log("response status: ", response.status);
      window.location.reload();
      //useRouter.push("/");
      //useRoute;
    };

    // определяем координаты, куда вывести подсказку кнопки
    const showMessageFunction = (event, message) => {
      const button = event.target;
      const rect = button.getBoundingClientRect();
      messageTop.value = rect.top + window.pageYOffset;
      messageLeft.value = rect.left + window.pageXOffset + rect.width + 10;
      messageText.value = message;
      showMessage.value = true;
    };

    return {
      show,
      clickCounters,
      clickHome,
      fetchCounters,
      logout,
      showCounters,
      showHome,
      username,
      searchQuery,
      selectedCounter,
      showCalc,
      reloadCounters,
      selectCounter,
      counters,
      totalCounters,
      handleSearchChange,
      calcRecords,
      totalCalcRecords,
      loading,
      totalCalcAmount,
      totalPayAmount,
      selectCalc,
      selectedCalc,
      payRecords,
      calcMonth,
      showPayTable,
      showCalcForm,
      selectCalcForm,
      lastCalc,
      reloadCalculation,
      showPayForm,
      selectPayForm,
      selectedAmount,
      selectedCounterData,
      callMassivePaymentFromParent,
      onRegisterMassivePayment,
      isCheckedForPayment,
      selectMassiveForPayment,
      selectedCalcsForPayment,
      onRegisterPrintCalculations,
      callPrintCalculationsFromParent,
      PrintCalculations,
      showUpdateCalc,
      selectUpdateCalc,
      calcForUpdate,
      showMessage,
      messageTop,
      messageLeft,
      showMessageFunction,
      messageText,
      onRegisterDeleteCalculations,
      callDeleteCalculationsFromParent,
      selectDeleteCalculations,
      selectUpdateCounter,
      showUpdateCounter,
      selectedCounterForUpdate,
      selectDeleteCounter,
      callDeleteCountersFromParent,
      deleteCounters,
      callDelete,
      onRegisterDeleteCounters,
      deletedCounters,
      numberOfDeletedCounters,
      clickArendators,
      showArendators,
      arendators,
      fetchArendators,
      totalArendators,
      selectArendForm,
      showArendForm,
      reloadArendators,
      showUpdateArendator,
      selectedArendatorForUpdate,
      selectUpdateArendator,
      clearSearch,
    };
  },
};
</script>

<template>
  <!-- clickHome: {{ showHome }}; showCounters: {{ showCounters }}; searchQuery:
  {{ searchQuery }}; loading: {{ loading }}; showCalc: {{ showCalc }}; lastCalc:
  {{ lastCalc }} <br />
  numberOfDeletedCounters: {{ numberOfDeletedCounters }} isCheckedForPayment:
  {{ isCheckedForPayment }} -->
  <div v-if="username">
    <my-dialog v-model:show="show" @close-dialog="show = false">
      <counter-form
        v-if="showCounters"
        :selectedCounterData="selectedCounterForUpdate"
        :showUpdateCounter="showUpdateCounter"
        @cancel="show = false"
        @submitForm="reloadCounters"
      />
      <payment-table
        v-if="showPayTable"
        :payRecords="payRecords"
        :calcMonth="calcMonth"
        @cancel="show = false"
      />
      <calc-form
        v-if="showCalcForm"
        :counterId="selectedCounter"
        :lastCalc="lastCalc"
        :showUpdateCalc="showUpdateCalc"
        :calcForUpdate="calcForUpdate"
        @cancel="show = false"
        @submitForm="reloadCalculation"
      />
      <payment-form
        v-if="showPayForm"
        :counterId="selectedCounter"
        :calcId="selectedCalc"
        :amount="selectedAmount"
        :selectedCalcsForPayment="selectedCalcsForPayment"
        @cancel="show = false"
        @submitForm="reloadCalculation"
      />
      <arendator-form
        v-if="showArendForm"
        :selectedArendatorData="selectedArendatorForUpdate"
        :showUpdateArendator="showUpdateArendator"
        @cancel="show = false"
        @submitForm="reloadArendators"
      />
    </my-dialog>
    <header>
      <div class="container">
        <div class="menu-space"></div>
        <div class="menu">
          <div class="menu-left">
            <button
              @click="
                clearSearch();
                clickCounters();
              "
              class="btn-menu white ff-500-18"
              :class="{ 'btn-active': showCounters }"
            >
              {{ $translate.t("btnCounters") }}
            </button>
            <button
              @click="
                clearSearch();
                clickArendators();
              "
              class="btn-menu white ff-500-18"
              :class="{ 'btn-active': showArendators }"
            >
              {{ $translate.t("btnTenants") }}
            </button>
            <button
              @click="
                clearSearch();
                clickHome();
              "
              class="btn-menu white ff-500-18"
              :class="{ 'btn-active': showHome }"
            >
              {{ $translate.t("btnDocum") }}
            </button>
            <div>
              Username: {{ username }}
              <span class="link-text indi" @click="logout">Разлогиниться</span>
            </div>
          </div>
          <div class="menu-right">
            <button
              @click="$translate.setLocale('ua')"
              class="btn-menu white ff-500-18"
              :class="{ 'btn-active': $translate.state.locale === 'ua' }"
            >
              Ua
            </button>
            <button
              @click="$translate.setLocale('en')"
              class="btn-menu white ff-500-18"
              :class="{ 'btn-active': $translate.state.locale === 'en' }"
            >
              En
            </button>
            <button
              @click="$translate.setLocale('ru')"
              class="btn-menu white ff-500-18"
              :class="{ 'btn-active': $translate.state.locale === 'ru' }"
            >
              Ru
            </button>
          </div>
        </div>
      </div>
    </header>
    <DocView v-if="showHome"></DocView>
    <div v-else class="container">
      <div class="sidebar">
        <div><img src="./components/icons/logo.svg" alt="logo" /></div>
        <button>
          <img src="./components/icons/folder.svg" alt="folder" />
        </button>
        <button
          @click="
            () => {
              if (isCheckedForPayment) {
                callMassivePaymentFromParent();
              }
            }
          "
          @mouseenter="showMessageFunction($event, $translate.t('btnPayment'))"
          @mouseleave="showMessage = false"
        >
          <img src="./components/icons/money.svg" alt="payment" />
        </button>
        <button
          @click="callPrintCalculationsFromParent"
          @mouseenter="showMessageFunction($event, $translate.t('btnPrint'))"
          @mouseleave="showMessage = false"
        >
          <img src="./components/icons/printer.svg" alt="printer" />
        </button>
        <button
          @click="callDelete"
          @mouseenter="showMessageFunction($event, $translate.t('btnDelete'))"
          @mouseleave="showMessage = false"
        >
          <img src="./components/icons/ben.svg" alt="ben" />
        </button>
        <div
          v-if="showMessage"
          class="message"
          :style="{
            position: 'absolute',
            top: `${messageTop}px`,
            left: `${messageLeft}px`,
          }"
        >
          <span class="ff-500-14 message-text">{{ messageText }}</span>
        </div>
      </div>
      <div class="right">
        <div v-if="loading" class="loading"></div>
        <TopNav
          v-if="!loading"
          @searchChange="handleSearchChange"
          :totalCounters="totalCounters"
          :showCounters="showCounters"
          :showArendators="showArendators"
          :totalArendators="totalArendators"
          :showCalc="showCalc"
          :totalCalcRecords="totalCalcRecords"
          :counterId="selectedCounter"
          :counterData="selectedCounterData"
          :totalCalcAmount="totalCalcAmount"
          :totalPayAmount="totalPayAmount"
          :numberOfDeletedCounters="numberOfDeletedCounters"
          @showModal="show = true"
          @show-calc-form="selectCalcForm"
          @showArendForm="selectArendForm"
        ></TopNav>
        <CountersTable
          v-if="showCounters && !loading"
          :counters="counters"
          :searchQuery="searchQuery"
          :show="show"
          @select-counter="selectCounter"
          @selectUpdateCounter="selectUpdateCounter"
          @selectDeleteCounter="selectDeleteCounter"
          @registerDeleteCounters="onRegisterDeleteCounters"
        ></CountersTable>

        <CalculationTable
          v-if="showCalc && !loading"
          @update:is-checked-for-payment="
            (newValue) => (isCheckedForPayment = newValue)
          "
          @registerMassivePayment="onRegisterMassivePayment"
          @registerPrintCalculations="onRegisterPrintCalculations"
          @registerDeleteCalculations="onRegisterDeleteCalculations"
          :show="show"
          :searchQuery="searchQuery"
          :calcRecords="calcRecords"
          :counterId="selectedCounter"
          :counterData="selectedCounterData"
          :totalCalcAmount="totalCalcAmount"
          :totalPayAmount="totalPayAmount"
          @select-calc="selectCalc"
          @select-pay="selectPayForm"
          @select-massive-for-payment="selectMassiveForPayment"
          @updateCalc="selectUpdateCalc"
          @deleteCalculations="selectDeleteCalculations"
        ></CalculationTable>
        <ArendatorsTable
          v-if="showArendators && !loading"
          :arendators="arendators"
          :searchQuery="searchQuery"
          :show="show"
          @selectUpdateArendator="selectUpdateArendator"
        >
        </ArendatorsTable>
      </div>
    </div>
  </div>
  <div v-else>Input login and password {{ username }}</div>
</template>

<style scoped>
.message {
  background: #171c26;
  /* Tooltip */
  box-shadow: 0px 0px 1px #ffffff, 0px 15px 35px -5px rgba(17, 24, 38, 0.35),
    0px 5px 15px -3px rgba(0, 0, 0, 0.2);
  border-radius: 8px;
  padding: 8px 12px;
  z-index: 1;
}
.message-text {
  color: #b9b6fa;
}
.btn-active {
  background: #4945c4;
  box-shadow: 0px 1px 1px rgba(0, 0, 0, 0.1), 0px 0px 0px 1px #5e5adb,
    0px 0px 0px 4px rgba(94, 90, 219, 0.4);
}
</style>
