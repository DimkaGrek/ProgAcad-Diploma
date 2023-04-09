import pdfMake from "pdfmake/build/pdfmake";

// import RobotoRegular from "../src/assets/fonts/Roboto/Roboto-Regular.ttf?url";
// import RobotoMedium from "../src/assets/fonts/Roboto/Roboto-Medium.ttf?url";
// import RobotoItalic from "../src/assets/fonts/Roboto/Roboto-Italic.ttf?url";
// import RobotoMediumItalic from "../src/assets/fonts/Roboto/Roboto-MediumItalic.ttf?url";

import {
  RobotoRegular,
  RobotoMedium,
  RobotoItalic,
  RobotoMediumItalic,
} from "@/assets/fonts/Roboto";

// const vfs = {
//   "Roboto-Regular.ttf": RobotoRegular,
//   "Roboto-Medium.ttf": RobotoMedium,
//   "Roboto-Italic.ttf": RobotoItalic,
//   "Roboto-MediumItalic.ttf": RobotoMediumItalic,
// };

async function urlToBuffer(url) {
  const response = await fetch(url);
  const buffer = await response.arrayBuffer();
  return buffer;
}

async function init() {
  pdfMake.vfs = {
    "Roboto-Regular.ttf": await urlToBuffer(RobotoRegular),
    "Roboto-Medium.ttf": await urlToBuffer(RobotoMedium),
    "Roboto-Italic.ttf": await urlToBuffer(RobotoItalic),
    "Roboto-MediumItalic.ttf": await urlToBuffer(RobotoMediumItalic),
  };
}

init();

export async function initPdfMake() {
  await init();
}

// pdfMake.vfs = vfs;

// import { pdfMake as pdfFonts } from "pdfmake/build/vfs_fonts";
// pdfMake.vfs = pdfFonts.pdfMake.vfs;
// pdfMake.fonts = {
//   Roboto: {
//     normal: "Roboto-Regular.ttf",
//     bold: "Roboto-Medium.ttf",
//     italics: "Roboto-Italic.ttf",
//     bolditalics: "Roboto-MediumItalic.ttf",
//   },
// };
export default pdfMake;
