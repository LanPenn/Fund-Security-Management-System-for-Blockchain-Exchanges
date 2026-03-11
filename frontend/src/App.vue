<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
export default {
  name: "App",
};
</script>

<style scoped>
/* 让根元素占据整个视口 */
#app {
  height: 100%;
  display: flex;
  flex-direction: column;
}
</style>
