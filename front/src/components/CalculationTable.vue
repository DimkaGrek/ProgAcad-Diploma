<script>
import moment from "moment";
import { ref, computed, watch, onMounted, inject } from "vue";
import CreatePDF from "./UI/CreatePDF.vue";

// import pdfMake, { initPdfMake } from "@/pdfmake.client";
// import pdfMake from "pdfmake/build/pdfmake";
// import * as pdfFonts from "pdfmake/build/vfs_fonts";
// pdfMake.vfs = pdfFonts.pdfMake.vfs;

// const pdfMake = require("pdfmake/build/pdfmake");
// const pdfFonts = require("pdfmake/build/vfs_fonts");
// pdfMake.vfs = pdfFonts.pdfMake.vfs;

export default {
  components: {
    CreatePDF,
  },
  props: [
    "calcRecords",
    "counterId",
    "show",
    "totalCalcAmount",
    "totalPayAmount",
    "counterData",
  ],
  emits: [
    "selectCalc",
    "selectPay",
    "select-calc",
    "select-pay",
    "registerMassivePayment",
    "update:is-checked-for-payment",
    "select-massive-for-payment",
    "registerPrintCalculations",
    "updateCalc",
    "registerDeleteCalculations",
    "deleteCalculations",
  ],

  setup(props, { emit }) {
    const showPDF = ref(null);
    const pdfIframe = ref(null);
    console.log("props counterData: ", props.counterData);
    const selectedIds = ref([]);
    const selectedColumn = ref("");
    const calculationsForPrint = ref(null);
    const isFromCalcTable = ref(false);
    const showAttentionMessage = ref(false); // показать окно предупреждения
    const formatDate = (date) => {
      return moment(date).format("DD.MM.YYYY");
    };

    const searchQuery = inject("searchQuery");
    const clearSearch = inject("clearSearch");

    function toggleSelectionAll(event) {
      if (event.target.checked) {
        selectedIds.value = [];
        for (let c of props.calcRecords) {
          selectedIds.value.push(c.id);
        }
      } else {
        selectedIds.value = [];
      }
    }

    // массив, который содержит ids квитанций для удаления
    const selectedIdForDelete = computed(() => {
      return selectedIds.value.filter((id) => {
        const record = props.calcRecords.find((item) => item.id === id);
        return record && record.payment === null;
      });
    });

    // массив, который содержит ids квитанций, необходимых для оплаты
    const selectedIdForPayment = computed(() => {
      return selectedIds.value.filter((id) => {
        const record = props.calcRecords.find((item) => item.id === id);
        return record && (record.payment === null || record.payment > 0);
      });
    });

    // считает общую сумму начислений только для квитанций, которые нужно оплатить
    const selectedAmountForPayment = computed(() => {
      return props.calcRecords.reduce((total, record) => {
        if (selectedIdForPayment.value.includes(record.id)) {
          total += record.payment > 0 ? record.payment : record.amount;
        }
        return total;
      }, 0);
    });

    // подсчитывает, какая общая сумма начислений выбранных квитанций
    const selectedTotalAmount = computed(() => {
      return props.calcRecords.reduce((total, record) => {
        if (selectedIds.value.includes(record.id)) {
          total += record.amount;
        }
        return total;
      }, 0);
    });

    // если выбрана хотя бы одна квитанция для оплаты, тогда открываем кнопку для оплаты
    const isСheckedForPayment = computed(() => {
      return selectedIdForPayment.value.length !== 0;
    });

    watch(
      selectedIdForPayment,
      () => {
        emit("update:is-checked-for-payment", isСheckedForPayment.value);
      },
      { immediate: false }
    );

    const callMassivePayment = () => {
      console.log("Функция массовой оплаты вызвана");
      if (selectedIdForPayment.value[0] !== undefined) {
        console.log("selectedIdForPayment: ", selectedIdForPayment.value[0]);
        console.log(
          "selectedAmountForPayment: ",
          selectedAmountForPayment.value
        );
        emit(
          "select-massive-for-payment",
          selectedIdForPayment.value,
          selectedAmountForPayment.value
        );
      } else console.log("Квитанции не выбраны");
    };

    const pdfDataURI = ref(null);

    const callPrintCalculations = async () => {
      console.log("Функция печати квитанций вызвана");
      calculationsForPrint.value = props.calcRecords.filter((record) =>
        selectedIdForPayment.value.includes(record.id)
      );
      isFromCalcTable.value = true;
      showPDF.value = true;

      // if (selectedIdForPayment.value[0] !== undefined) {
      //   await initPdfMake();
      //   const selectedCalcs = props.calcRecords.filter((record) =>
      //     selectedIdForPayment.value.includes(record.id)
      //   );
      //   const pages = [];
      //   const itemsPerPage = 3;
      //   for (let i = 0; i < selectedCalcs.length; i += itemsPerPage) {
      //     const page = selectedCalcs.slice(i, i + itemsPerPage);
      //     pages.push(page);
      //   }
      //   console.log("selectedCalcs: ", selectedCalcs[0].id);
      //   console.log("props.counterData", props.counterData);
      //   console.log("props.counterId", props.counterId);
      //   console.log("pages[]: ", pages);
      //   const content = [];
      //   for (let i = 0; i < pages.length; i++) {
      //     for (const calc of pages[i]) {
      //       content.push(
      //         {
      //           text: [
      //             { text: "ПОВІДОМЛЕННЯ № ", style: "header" },
      //             {
      //               text: calc.id,
      //               style: "header",
      //               lineHeight: 2,
      //             },
      //           ],
      //           margin: [0, 10, 0, 0],
      //         },
      //         {
      //           text: [
      //             { text: "ПІБ: ", bold: true },
      //             {
      //               text: calc.name + " " + calc.surname,
      //               style: "textItalic",
      //               lineHeight: 1.5,
      //             },
      //           ],
      //         },
      //         {
      //           text: [
      //             { text: "№ Павільону:    ", bold: true },
      //             {
      //               text: props.counterData.pavilion + "        ",
      //               style: "textItalic",
      //             },
      //             { text: "№ Місця:    ", bold: true },
      //             {
      //               text: props.counterData.place + "\u00A0".repeat(35),
      //               style: "textItalic",
      //             },
      //             {
      //               text: "Період споживання:    ",
      //               bold: true,
      //               alignment: "right",
      //             },
      //             {
      //               text: calc.month,
      //               style: "textItalic",
      //               lineHeight: 1.5,
      //             },
      //           ],
      //         },
      //         {
      //           // layout: "lightHorizontalLines", // optional
      //           table: {
      //             // headers are automatically repeated if the table spans over multiple pages
      //             // you can declare how many rows should be treated as headers
      //             // headerRows: 1,
      //             widths: ["*", "auto"],

      //             body: [
      //               [
      //                 {
      //                   text: "Найменування послуг",
      //                   style: "tableTextBold",
      //                 },
      //                 {
      //                   text: "Сумма до оплати, грн.",
      //                   style: "tableTextBold",
      //                 },
      //               ],
      //               [
      //                 {
      //                   text: "Відшкодування експлуатаційних послуг",
      //                   alignment: "center",
      //                   fontSize: 12,
      //                 },
      //                 {
      //                   text: calc.amount.toFixed(2),
      //                   style: "tableTextRight",
      //                 },
      //               ],
      //               [
      //                 { text: "Борг:", style: "tableTextRight" },
      //                 {
      //                   text: (
      //                     props.totalCalcAmount -
      //                     props.totalPayAmount -
      //                     calc.amount
      //                   ).toFixed(2),
      //                   style: "tableTextRight",
      //                 },
      //               ],
      //               [
      //                 {
      //                   text: "Всього до сплати, грн.:",
      //                   style: "tableTextRight",
      //                   bold: true,
      //                 },
      //                 {
      //                   text: (
      //                     props.totalCalcAmount - props.totalPayAmount
      //                   ).toFixed(2),
      //                   style: "tableTextRight",
      //                   bold: true,
      //                 },
      //               ],
      //             ],
      //           },
      //         },
      //         {
      //           text: [
      //             {
      //               text: "* За період спожито електроенергії: ",
      //               fontSize: 6,
      //             },
      //             {
      //               text: calc.countNow - calc.countBefore + " кВт",
      //               fontSize: 6,
      //               lineHeight: 1.2,
      //             },
      //           ],
      //           margin: [0, 3, 0, 0],
      //         },

      //         {
      //           text: "―".repeat(126),
      //           fontSize: 6,
      //           lineHeight: 0.6,
      //           margin: [-40, 0, -10, 0],
      //         },

      //         {
      //           text: "Додаток (спожито електроенергії)",
      //           style: "header",
      //           margin: [0, 5, 0, 5],
      //         },
      //         {
      //           table: {
      //             widths: ["auto", "auto", "auto"],
      //             body: [
      //               [
      //                 {
      //                   text: "Поточні показники, кВтг",
      //                   style: "tableTextBold",
      //                 },
      //                 {
      //                   text: "Попередні показники, кВтг",
      //                   style: "tableTextBold",
      //                 },
      //                 { text: "Використано, кВтг", style: "tableTextBold" },
      //               ],
      //               [
      //                 {
      //                   text: calc.countNow,
      //                   style: "tableTextCenter",
      //                 },
      //                 {
      //                   text: calc.countBefore,
      //                   style: "tableTextCenter",
      //                 },
      //                 {
      //                   text: calc.countNow - calc.countBefore,
      //                   style: "tableTextCenter",
      //                 },
      //               ],
      //             ],
      //           },
      //         },

      //         {
      //           text: "―".repeat(126), // Может потребоваться изменить количество символов в зависимости от размера шрифта и ширины страницы
      //           fontSize: 6,
      //           lineHeight: 0.6,
      //           margin: [-40, 5, -10, 0],
      //         }
      //       );
      //     }
      //     if (i !== pages.length - 1) {
      //       content.push({
      //         text: "",
      //         pageBreak: "after", // Вставляем разрыв страницы после этого элемента
      //       });
      //     }
      //   } // end for

      //   let docDefinition = {
      //     // a string or { width: number, height: number }
      //     pageSize: "A4",
      //     // [left, top, right, bottom] or [horizontal, vertical] or just a number for equal margins
      //     pageMargins: [40, 20, 10, 10],
      //     content: content,

      //     styles: {
      //       header: {
      //         fontSize: 14,
      //         bold: true,
      //         alignment: "center",
      //       },
      //       textItalic: {
      //         italics: true,
      //         // alignment: "right",
      //       },
      //       tableTextBold: {
      //         fontSize: 12,
      //         bold: true,
      //         alignment: "center",
      //       },
      //       tableTextRight: {
      //         alignment: "right",
      //         fontSize: 12,
      //       },
      //       tableTextCenter: {
      //         alignment: "center",
      //         fontSize: 12,
      //       },
      //     },
      //   };
      //   console.log("open PDF...");
      //   const pdfDocGenerator = pdfMake.createPdf(docDefinition);
      //   // console.log("pdfDocGenerator : ", pdfDocGenerator);

      //   const pdfBlob = await new Promise((resolve, reject) => {
      //     pdfDocGenerator.getBlob((blob) => {
      //       if (blob) {
      //         resolve(blob);
      //       } else {
      //         reject("Error creating Blob");
      //       }
      //     });
      //   });

      //   pdfDataURI.value = URL.createObjectURL(pdfBlob);
      //   // version with not work print
      //   // pdfDataURI.value = await new Promise((resolve) => {
      //   //   pdfDocGenerator.getBase64((base64) => {
      //   //     resolve(`data:application/pdf;base64,${base64}`);
      //   //   });
      //   // });

      //   showPDF.value = true;
      //   // console.log("pdfDataURI: ", pdfDataURI.value);
      // } else console.log("Квитанции не выбраны");
    };

    const printPDF = () => {
      if (pdfIframe.value) {
        pdfIframe.value.contentWindow.print();
      }
    };

    const getCurrentDate = () => {
      const today = new Date();
      const day = String(today.getDate()).padStart(2, "0");
      const month = String(today.getMonth() + 1).padStart(2, "0"); // Месяцы считаются с 0 до 11
      const year = today.getFullYear();

      return `${day}-${month}-${year}`;
    };

    const savePDF = () => {
      const link = document.createElement("a");
      link.href = pdfDataURI.value;
      link.download = "invoicePDF-" + getCurrentDate() + ".pdf";
      link.click();
    };

    const callDeleteCalculations = () => {
      console.log("Функция удаления квитанции вызвана...");
      if (selectedIdForDelete.value[0] !== undefined) {
        showAttentionMessage.value = true;
      }
    };

    onMounted(() => {
      emit("registerMassivePayment", callMassivePayment);
      emit("registerDeleteCalculations", callDeleteCalculations);
    });

    function selectCalc(calcId, month) {
      console.log("Выбрали квитанцию: ", calcId);
      console.log("month: ", month);
      emit("select-calc", calcId, month);
    }

    function selectPay(calcId, amount) {
      console.log("Выбрали оплатить квитанцию: ", calcId);
      console.log("сумма оплати: ", amount);

      // cчитаем переплату, чтобы была возможность учесть ее при оплате
      const overpayment = props.calcRecords.reduce((total, record) => {
        total += record.payment < 0 ? Math.abs(record.payment) : 0;
        return total;
      }, 0);
      console.log("overpayment: ", overpayment);
      emit("select-pay", calcId, amount, props.counterId, overpayment);
    }

    function updateCalc(calcId) {
      console.log("function updateCalc...");
      emit("updateCalc", calcId);
    }

    function deleteCalculations() {
      showAttentionMessage.value = false;
      emit("deleteCalculations", selectedIdForDelete.value, props.counterId);
    }

    const sortedCalc = computed(() => {
      console.log("Props show: ", props.show);
      return [...props.calcRecords].sort((calc1, calc2) =>
        String(calc1[selectedColumn.value])?.localeCompare(
          String(calc2[selectedColumn.value])
        )
      );
    });

    const sortedAndSearchCalc = computed(() => {
      return sortedCalc.value
        .filter(
          (calc) =>
            calc.month
              .toLowerCase()
              .includes(searchQuery.value.toLowerCase()) ||
            formatDate(calc.date)
              .toLowerCase()
              .includes(searchQuery.value.toLowerCase()) ||
            String(calc.countNow).includes(searchQuery.value) ||
            String(calc.countBefore).includes(searchQuery.value) ||
            String(calc.difference).includes(searchQuery.value) ||
            String(calc.rate).includes(searchQuery.value) ||
            String(calc.amount).includes(searchQuery.value) ||
            calc.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
            calc.surname.toLowerCase().includes(searchQuery.value.toLowerCase())
        )
        .reverse();
    });

    return {
      formatDate,
      selectCalc,
      selectPay,
      sortedCalc,
      sortedAndSearchCalc,
      selectedIds,
      toggleSelectionAll,
      selectedColumn,
      selectedTotalAmount,
      selectedIdForPayment,
      selectedAmountForPayment,
      isСheckedForPayment,
      callPrintCalculations,
      updateCalc,
      selectedIdForDelete,
      showAttentionMessage,
      deleteCalculations,
      pdfDataURI,
      showPDF,
      pdfIframe,
      printPDF,
      savePDF,
      searchQuery,
      clearSearch,
      calculationsForPrint,
      isFromCalcTable,
      props,
    };
  },
};
</script>

<template>
  <!-- selectedIds: {{ selectedIds }} selectedColumn:
  {{ selectedColumn }} selectedTotalAmount {{ selectedTotalAmount }} <br />
  selectedIdForPayment: {{ selectedIdForPayment }} selectedAmountForPayment:
  {{ selectedAmountForPayment }} <br />
  isСheckedForPayment: {{ isСheckedForPayment }} -->

  <CreatePDF
    v-if="showPDF"
    :calculationsForPrint="calculationsForPrint"
    :counterData="counterData"
    :totalCalcAmount="totalCalcAmount"
    :totalPayAmount="totalPayAmount"
    :isFromCalcTable="isFromCalcTable"
    @closeFrame="(showPDF = false), (isFromCalcTable = false)"
  />

  <!-- <div class="modal" v-if="showPDF">
    <div class="modal-content">
      <div class="modal-header">
        <img @click="savePDF" src="./icons/save.png" alt="save" />
        <img @click="printPDF" src="./icons/print.png" alt="print" />
        <img @click="showPDF = false" src="./icons/cancel.png" alt="close" />
      </div>
      <iframe ref="pdfIframe" class="pdf-iframe" :src="pdfDataURI"></iframe>
    </div>
  </div> -->

  <div class="table-scroll">
    <div v-if="showAttentionMessage" class="attention">
      <h1 class="ff-500-18">{{ $translate.t("areYouSure") }}</h1>
      <div class="add-footer">
        <button @click="showAttentionMessage = false" class="btn-tbl ff-500-14">
          {{ $translate.t("close") }}
        </button>
        <button @click="deleteCalculations" class="btn-tbl ff-500-14">
          {{ $translate.t("confirm") }}
        </button>
      </div>
    </div>
    <table>
      <thead :class="{ 'disable-sticky': show }">
        <tr>
          <th class="th-checkbox">
            <label for="checkbox" class="checkbox-label">
              <input
                @change="toggleSelectionAll"
                id="checkbox"
                type="checkbox"
                class="checkbox-input"
              />
              <span class="checkbox-custom"></span>
            </label>
          </th>
          <th @click="selectedColumn = 'month'">
            {{ $translate.t("tablePeriodUpper") }}
          </th>
          <th>{{ $translate.t("tableDateUpper") }}</th>
          <th>{{ $translate.t("tableTenantUpper") }}</th>
          <th>{{ $translate.t("tableCurrReadUpper") }}</th>
          <th>{{ $translate.t("tablePrevReadUpper") }}</th>
          <th>{{ $translate.t("tableDifferenceUpper") }}</th>
          <th>{{ $translate.t("tableRateUpper") }}</th>
          <th>{{ $translate.t("tableAmountUpper") }}</th>
          <th>{{ $translate.t("infoUpper") }}</th>
          <th>{{ $translate.t("tablePaymentUpper") }}</th>
        </tr>
      </thead>
      <tbody>
        <transition-group name="counter-list">
          <!-- @click="$emit('select-calc', c.id)" -->
          <tr
            v-for="c in sortedAndSearchCalc"
            :key="c.id"
            :class="{ 'tr-checked': selectedIds.includes(c.id) }"
          >
            <td>
              <label :for="c.id" class="checkbox-label">
                <input
                  :id="c.id"
                  type="checkbox"
                  :value="c.id"
                  v-model="selectedIds"
                  class="checkbox-input"
                />
                <span class="checkbox-custom"></span>
              </label>
            </td>
            <td
              @click.prevent="
                c.payment != null ? selectCalc(c.id, c.month) : null
              "
            >
              <span
                @click="updateCalc(c.id)"
                :class="c.payment == null ? ['link-text', 'indi'] : []"
                >{{ c.month }}</span
              >
            </td>
            <td
              @click.prevent="
                c.payment != null ? selectCalc(c.id, c.month) : null
              "
            >
              {{ formatDate(c.date) }}
            </td>
            <td
              @click.prevent="
                c.payment != null ? selectCalc(c.id, c.month) : null
              "
            >
              {{ c.surname }} {{ c.name }}
            </td>
            <td
              @click.prevent="
                c.payment != null ? selectCalc(c.id, c.month) : null
              "
            >
              {{ c.countNow }}
            </td>
            <td
              @click.prevent="
                c.payment != null ? selectCalc(c.id, c.month) : null
              "
            >
              {{ c.countBefore }}
            </td>
            <td
              @click.prevent="
                c.payment != null ? selectCalc(c.id, c.month) : null
              "
            >
              {{ c.difference }}
            </td>
            <td
              @click.prevent="
                c.payment != null ? selectCalc(c.id, c.month) : null
              "
            >
              {{ c.rate }}
            </td>
            <td
              @click.prevent="
                c.payment != null ? selectCalc(c.id, c.month) : null
              "
            >
              {{ c.amount.toFixed(2) }}
            </td>
            <td
              @click.prevent="
                c.payment != null ? selectCalc(c.id, c.month) : null
              "
            >
              {{ c.notes }}
            </td>
            <td
              @click.prevent="
                c.payment != null && c.payment <= 0
                  ? selectCalc(c.id, c.month)
                  : null
              "
            >
              <div>
                <span
                  v-if="c.payment == null"
                  @click="selectPay(c.id, c.amount)"
                  class="ff-500-14 link-text"
                  :class="{ needpay: c.payment == null }"
                  >{{ $translate.t("payment") }}</span
                >
                <span
                  v-else-if="
                    c.payment == 0 || (c.payment > -0.001 && c.payment < 0)
                  "
                  :class="{ paid: c.payment == 0 }"
                  >{{ $translate.t("paid") }}</span
                >
                <span
                  v-else-if="c.payment > 0"
                  class="link-text"
                  :class="{ debt: c.payment > 0 }"
                  @click="selectPay(c.id, c.payment.toFixed(2))"
                  >{{ c.payment.toFixed(2) }}</span
                >
                <span
                  v-else-if="c.payment < 0.001"
                  :class="{ overpay: c.payment < 0.001 }"
                  >{{ Math.abs(c.payment.toFixed(2)) }}</span
                >

                <span v-else>{{ c.payment.toFixed(2) }}</span>
              </div>
            </td>
          </tr>
        </transition-group>
      </tbody>
    </table>
  </div>
  <div v-if="selectedIds.length > 0" class="info-text selected-text">
    {{ $translate.t("selectAll") }}
    <span class="black">{{ selectedIds.length }}</span>
    {{ $translate.t("accuralsForAmount") }}
    <span class="black">{{ selectedTotalAmount.toFixed(2) }}</span>
    <template v-if="selectedIdForPayment.length > 0">
      / {{ $translate.t("selectedForPayment") }}
      <span class="black">{{ selectedIdForPayment.length }}</span>
      {{ $translate.t("accuralsForAmount") }}
      <span class="black">{{ selectedAmountForPayment.toFixed(2) }}</span>
    </template>
  </div>
</template>

<style scoped>
.selected-text {
  margin-top: 10px;
  margin-left: 20px;
  margin-bottom: 10px;
}
.black {
  color: black;
  background-color: #ededfc;
  padding: 3px;
}
.attention {
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  position: absolute;
  z-index: 1;
  margin: auto;
  background: #f7f8fa;
  padding: 10px;
  border: 1px solid #aa5b00;
  border-radius: 8px;
  text-align: center;
}

.modal {
  display: block;
  position: fixed;
  z-index: 1000;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  overflow: auto;
  background-color: rgba(0, 0, 0, 0.5);
}

.modal-content {
  background-color: #fff;
  margin: 15% auto;
  padding: 20px;
  border: 1px solid #888;
  width: 80%;
  height: 70%;
}

.close-button {
  color: #aaa;
  float: right;
  font-size: 28px;
  font-weight: bold;
  cursor: pointer;
}

.pdf-iframe {
  width: 100%;
  height: calc(100% - 30px);
}

.modal-header {
  display: flex;
  justify-content: end;
  align-items: center;
  gap: 10px;
  padding: 5px 0px;
}
.modal-header img {
  width: 25px;
  cursor: pointer;
}
</style>
