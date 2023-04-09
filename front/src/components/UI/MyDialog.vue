<script>
export default {
  name: "my-dialog",
  props: {
    show: {
      type: Boolean,
      default: false,
    },
  },
};
</script>

<template>
  <div class="dialog" v-if="show">
    <div @click.stop @keydown.enter.stop class="dialog__content">
      <slot></slot>
    </div>
  </div>
</template>

<style scoped>
.dialog {
  top: 0;
  bottom: 0;
  right: 0;
  left: 0;
  background: rgba(0, 0, 0, 0.8);
  position: fixed;
  z-index: 1;
  display: flex;
  overflow-y: auto;
}
.dialog__content {
  margin: auto;
}
</style>
