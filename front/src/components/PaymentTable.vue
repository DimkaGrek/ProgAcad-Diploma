<script>
import moment from "moment";
export default {
  props: ["payRecords", "calcMonth"],
  setup(props) {
    const formatDate = (date) => {
      return moment(date).format("DD.MM.YYYY, h:mm");
    };
    return { props, formatDate };
  },
};
</script>

<template>
  <div class="pop-up">
    <div class="add-header">
      <h1 class="ff-500-18">
        {{ $translate.t("PaymentsFor") }} {{ calcMonth }}
      </h1>
    </div>
    <div class="pop-up-content">
      <table>
        <thead>
          <tr class="no-wrap">
            <th>{{ $translate.t("tableDateUpper") }}</th>
            <th>{{ $translate.t("formReceiptNumberUpper") }}</th>
            <th>{{ $translate.t("tableAmountUpper") }}</th>
            <th>{{ $translate.t("infoUpper") }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="p in payRecords" :key="p.id" class="no-wrap">
            <td>{{ formatDate(p.date) }}</td>
            <td>{{ p.receiptId }}</td>
            <td>{{ p.amount.toFixed(2) }}</td>
            <td :class="{ wrap: p.notes.length > 100 }">{{ p.notes }}</td>
          </tr>
        </tbody>
      </table>
    </div>
    <div class="add-footer close">
      <button @click="$emit('cancel')" class="btn-tbl ff-500-14">
        {{ $translate.t("close") }}
      </button>
    </div>
  </div>
</template>

<style scoped>
.pop-up {
  width: 100%;
  background: #f7f8fa;
  box-shadow: 0px 0px 0px 1px rgba(136, 143, 170, 0.1),
    0px 30px 70px rgba(26, 34, 64, 0.15), 0px 10px 30px rgba(0, 0, 0, 0.2);
  border-radius: 12px;
}
.pop-up-content {
  padding: 5px;
  margin: auto;
}
.close {
  margin-top: 15px;
  margin-right: 15px;
  padding-bottom: 10px;
}
.no-wrap {
  white-space: nowrap;
}
.wrap {
  white-space: normal;
}
</style>
