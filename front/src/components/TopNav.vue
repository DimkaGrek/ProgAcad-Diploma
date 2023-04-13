<script>
import { computed, inject } from "vue";
import UserDropdown from "./UserDropdown.vue";

export default {
  components: {
    UserDropdown,
  },
  props: [
    "totalCounters",
    "showCounters",
    "showCalc",
    "totalCalcRecords",
    "counterId",
    "totalCalcAmount",
    "totalPayAmount",
    "counterData",
    "numberOfDeletedCounters",
    "showArendators",
    "totalArendators",
  ],
  emits: ["showModal", "show-calc-form", "showArendForm"],
  setup(props) {
    console.log("props totalCounters: ", props.totalCounters);
    console.log("props showCounters: ", props.showCounters);
    console.log("props showCalc: ", props.showCalc);
    console.log("props totalCalcAmount: ", props.totalCalcAmount);
    console.log("props totalPayAmount: ", props.totalPayAmount);
    console.log("props counterData: ", props.counterData);

    const searchQuery = inject("searchQuery");
    const clearSearch = inject("clearSearch");

    // debtPayment.value = props.totalCalcAmount - props.totalPayAmount;
    const debtPayment = computed(() => {
      return props.totalCalcAmount - props.totalPayAmount;
    });
    console.log("debtPayment:", debtPayment.value);
    // totalCalc.value = props.totalCalcAmount && props.totalCalcAmount.toFixed(2);
    // totalPay.value =
    //   props.totalPayAmount && parseFloat(props.totalPayAmount).toFixed(2);
    const totalCalc = computed(() => {
      return props.totalCalcAmount && props.totalCalcAmount.toFixed(2);
    });

    const totalPay = computed(() => {
      return props.totalPayAmount && props.totalPayAmount.toFixed(2);
    });

    console.log("totalCalc: ", totalCalc.value);
    console.log("totalPay: ", totalPay.value);
    return {
      debtPayment,
      totalCalc,
      totalPay,
      searchQuery,
      clearSearch,
    };
  },
};
</script>

<template>
  <!-- totalCounters {{ totalCounters }} localSearch {{ localSearch }} -->
  <div class="topnav">
    <div class="topnav-title">
      <template v-if="showCounters">
        <h2>{{ $translate.t("titleCounters") }}</h2>
        <div class="count indi">
          {{ totalCounters }}
        </div>
      </template>
      <template v-if="showArendators">
        <h2>{{ $translate.t("titleTenants") }}</h2>
        <div class="count indi">
          {{ totalArendators }}
        </div>
      </template>
      <template v-if="showCalc">
        <h2>
          {{ $translate.t("counter") }}:
          <span class="indi">{{ counterData.number }}</span>
        </h2>
        <h3>
          {{ $translate.t("formPavilion") }}:
          <span class="indi">{{ counterData.pavilion }}</span>
        </h3>
        <h3>
          {{ $translate.t("formPlace") }}:
          <span class="indi">{{ counterData.place }}</span>
        </h3>
      </template>
    </div>
    <div class="topnav-right">
      <!-- <img
        src="./icons/notify_icon.png"
        alt="notify_icon"
        width="24"
        height="24"
      />
      <img src="./icons/help_icon.png" alt="help_icon" width="24" height="24" /> -->
      <user-dropdown />
    </div>
  </div>
  <div class="infobar">
    <div class="info-search">
      <div class="search-part">
        <button class="btn-tbl">
          <img src="./icons/data-table_icon.png" alt="data-table_icon" />
        </button>
        <div class="search">
          <div class="filter">
            <img
              src="./icons/filter_icon.png"
              alt="filter_icon"
              width="16"
              height="16"
            />
            <select name="select" id="">
              <option value="all">{{ $translate.t("all") }}</option>
            </select>
          </div>
          <div class="search-input">
            <img
              src="./icons/search_icon.png"
              alt="search_icon"
              width="16"
              height="16"
            />
            <input
              v-model="searchQuery"
              type="text"
              name="search"
              id=""
              placeholder="Search"
            />
            <img
              src="./icons/shortcut_icon.png"
              alt="shortcut_icon"
              width="16"
              height="16"
            />
          </div>
        </div>
      </div>

      <button
        v-if="showCounters"
        @click="$emit('showModal')"
        class="btn-add ff-500-14"
      >
        + {{ $translate.t("btnAddCounter") }}
      </button>
      <button
        v-if="showArendators"
        @click="$emit('showArendForm')"
        class="btn-add ff-500-14"
      >
        + {{ $translate.t("btnAddTenant") }}
      </button>
      <button
        v-if="showCalc"
        @click="$emit('show-calc-form', counterId)"
        class="btn-add ff-500-14"
      >
        + {{ $translate.t("btnAddAccrual") }}
      </button>
    </div>
  </div>

  <div class="info-stat">
    <div class="info-item">
      <div class="info-item-top">
        <template v-if="showCounters">
          <div class="info-text">
            <a href="">{{ $translate.t("all") }}</a>
          </div>
          <div class="count indi">{{ totalCounters }}</div>
        </template>
        <template v-if="showArendators">
          <div class="info-text">
            <a href="">{{ $translate.t("all") }}</a>
          </div>
          <div class="count indi">{{ totalArendators }}</div>
        </template>
        <template v-if="showCalc">
          <div class="info-text">
            <a href="">{{ $translate.t("totalCalculated") }}</a>
          </div>
          <div class="count indi-500">{{ totalCalc }}</div>
        </template>
      </div>
      <div class="info-select"><hr /></div>
    </div>
    <div class="info-item">
      <div class="info-item-top">
        <template v-if="showCounters">
          <div class="info-text">
            <a href="">{{ $translate.t("common") }}</a>
          </div>
          <div class="count indi">5</div>
        </template>
        <template v-if="showCalc">
          <div class="info-text">
            <a href="">{{ $translate.t("totalPay") }}</a>
          </div>
          <div class="count indi-500">{{ totalPay }}</div>
        </template>
      </div>
      <!-- <div class="info-select"><hr></div> -->
    </div>
    <div class="info-item">
      <div class="info-item-top">
        <template v-if="showCounters">
          <div class="info-text">
            <a href="">{{ $translate.t("inactive") }}</a>
          </div>
          <div class="count indi">{{ numberOfDeletedCounters }}</div>
        </template>
        <template v-if="showCalc && debtPayment !== 0">
          <div class="info-text">
            <span v-if="debtPayment > 0" class="indi">{{
              $translate.t("debt")
            }}</span>
            <span v-else class="indi">{{ $translate.t("overpayment") }}</span>
          </div>
          <div v-if="debtPayment > 0" class="count red">
            {{ debtPayment.toFixed(2) }}
          </div>
          <div v-else class="count">{{ Math.abs(debtPayment.toFixed(2)) }}</div>
        </template>
      </div>
      <!-- <div class="info-select"><hr></div> -->
    </div>
  </div>
</template>

<style scoped></style>
