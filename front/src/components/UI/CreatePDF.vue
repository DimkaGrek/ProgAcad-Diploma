<script>
import { onMounted, ref } from "vue";
import pdfMake, { initPdfMake } from "@/pdfmake.client";

export default {
  props: [
    "calculationsForPrint",
    "counterData",
    "totalCalcAmount",
    "totalPayAmount",
    "isFromCalcTable",
  ],

  setup(props) {
    const pdfDataURI = ref(null);
    const pdfIframe = ref(null);

    const PrintCalculations = async () => {
      console.log("Функция печати квитанций вызвана");

      await initPdfMake();
      const selectedCalcs = props.calculationsForPrint;
      const pages = [];
      const itemsPerPage = 3;
      for (let i = 0; i < selectedCalcs.length; i += itemsPerPage) {
        const page = selectedCalcs.slice(i, i + itemsPerPage);
        pages.push(page);
      }
      console.log("selectedCalcs: ", selectedCalcs[0].id);
      console.log("pages[]: ", pages);
      const content = [];
      for (let i = 0; i < pages.length; i++) {
        for (const calc of pages[i]) {
          if (props.isFromCalcTable) {
            calc.pavilion = props.counterData.pavilion;
            calc.place = props.counterData.place;
            calc.totalCalcAmount = props.totalCalcAmount;
            calc.totalPayAmount = props.totalPayAmount;
          }
          content.push(
            {
              text: [
                { text: "ПОВІДОМЛЕННЯ № ", style: "header" },
                {
                  text: calc.id,
                  style: "header",
                  lineHeight: 2,
                },
              ],
              margin: [0, 10, 0, 0],
            },
            {
              text: [
                { text: "ПІБ: ", bold: true },
                {
                  text: calc.name + " " + calc.surname,
                  style: "textItalic",
                  lineHeight: 1.5,
                },
              ],
            },
            {
              text: [
                { text: "№ Павільону:    ", bold: true },
                {
                  text: calc.pavilion + "        ",
                  style: "textItalic",
                },
                { text: "№ Місця:    ", bold: true },
                {
                  text: calc.place + "\u00A0".repeat(35),
                  style: "textItalic",
                },
                {
                  text: "Період споживання:    ",
                  bold: true,
                  alignment: "right",
                },
                {
                  text: calc.month,
                  style: "textItalic",
                  lineHeight: 1.5,
                },
              ],
            },
            {
              // layout: "lightHorizontalLines", // optional
              table: {
                // headers are automatically repeated if the table spans over multiple pages
                // you can declare how many rows should be treated as headers
                // headerRows: 1,
                widths: ["*", "auto"],

                body: [
                  [
                    {
                      text: "Найменування послуг",
                      style: "tableTextBold",
                    },
                    {
                      text: "Сумма до оплати, грн.",
                      style: "tableTextBold",
                    },
                  ],
                  [
                    {
                      text: "Відшкодування експлуатаційних послуг",
                      alignment: "center",
                      fontSize: 12,
                    },
                    {
                      text: calc.amount.toFixed(2),
                      style: "tableTextRight",
                    },
                  ],
                  [
                    { text: "Борг:", style: "tableTextRight" },
                    {
                      text: (
                        calc.totalCalcAmount -
                        calc.totalPayAmount -
                        calc.amount
                      ).toFixed(2),
                      style: "tableTextRight",
                    },
                  ],
                  [
                    {
                      text: "Всього до сплати, грн.:",
                      style: "tableTextRight",
                      bold: true,
                    },
                    {
                      text: (
                        calc.totalCalcAmount - calc.totalPayAmount
                      ).toFixed(2),
                      style: "tableTextRight",
                      bold: true,
                    },
                  ],
                ],
              },
            },
            {
              text: [
                {
                  text: "* За період спожито електроенергії: ",
                  fontSize: 6,
                },
                {
                  text: calc.countNow - calc.countBefore + " кВт",
                  fontSize: 6,
                  lineHeight: 1.2,
                },
              ],
              margin: [0, 3, 0, 0],
            },

            {
              text: "―".repeat(126),
              fontSize: 6,
              lineHeight: 0.6,
              margin: [-40, 0, -10, 0],
            },

            {
              text: "Додаток (спожито електроенергії)",
              style: "header",
              margin: [0, 5, 0, 5],
            },
            {
              table: {
                widths: ["auto", "auto", "auto"],
                body: [
                  [
                    {
                      text: "Поточні показники, кВтг",
                      style: "tableTextBold",
                    },
                    {
                      text: "Попередні показники, кВтг",
                      style: "tableTextBold",
                    },
                    { text: "Використано, кВтг", style: "tableTextBold" },
                  ],
                  [
                    {
                      text: calc.countNow,
                      style: "tableTextCenter",
                    },
                    {
                      text: calc.countBefore,
                      style: "tableTextCenter",
                    },
                    {
                      text: calc.countNow - calc.countBefore,
                      style: "tableTextCenter",
                    },
                  ],
                ],
              },
            },

            {
              text: "―".repeat(126), // Может потребоваться изменить количество символов в зависимости от размера шрифта и ширины страницы
              fontSize: 6,
              lineHeight: 0.6,
              margin: [-40, 5, -10, 0],
            }
          );
        }
        if (i !== pages.length - 1) {
          content.push({
            text: "",
            pageBreak: "after", // Вставляем разрыв страницы после этого элемента
          });
        }
      } // end for

      let docDefinition = {
        // a string or { width: number, height: number }
        pageSize: "A4",
        // [left, top, right, bottom] or [horizontal, vertical] or just a number for equal margins
        pageMargins: [40, 20, 10, 10],
        content: content,

        styles: {
          header: {
            fontSize: 14,
            bold: true,
            alignment: "center",
          },
          textItalic: {
            italics: true,
            // alignment: "right",
          },
          tableTextBold: {
            fontSize: 12,
            bold: true,
            alignment: "center",
          },
          tableTextRight: {
            alignment: "right",
            fontSize: 12,
          },
          tableTextCenter: {
            alignment: "center",
            fontSize: 12,
          },
        },
      };
      console.log("open PDF...");
      const pdfDocGenerator = pdfMake.createPdf(docDefinition);
      // console.log("pdfDocGenerator : ", pdfDocGenerator);

      const pdfBlob = await new Promise((resolve, reject) => {
        pdfDocGenerator.getBlob((blob) => {
          if (blob) {
            resolve(blob);
          } else {
            reject("Error creating Blob");
          }
        });
      });

      pdfDataURI.value = URL.createObjectURL(pdfBlob);
      // version with not work print
      // pdfDataURI.value = await new Promise((resolve) => {
      //   pdfDocGenerator.getBase64((base64) => {
      //     resolve(`data:application/pdf;base64,${base64}`);
      //   });
      // });

      // showPDF.value = true;
      // console.log("pdfDataURI: ", pdfDataURI.value);
    };

    onMounted(PrintCalculations);

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

    return { PrintCalculations, pdfDataURI, printPDF, savePDF, pdfIframe };
  },
};
</script>

<template>
  <div class="modal">
    <div class="modal-content">
      <div class="modal-header">
        <img @click="savePDF" src="../icons/save.png" alt="save" />
        <img @click="printPDF" src="../icons/print.png" alt="print" />
        <img
          @click="$emit('closeFrame')"
          src="../icons/cancel.png"
          alt="close"
        />
      </div>
      <iframe ref="pdfIframe" class="pdf-iframe" :src="pdfDataURI"></iframe>
    </div>
  </div>
</template>

<style scoped>
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
