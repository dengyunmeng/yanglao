<template>
  <div id="app">
    <router-view />
    <!-- 全局「问医生」浮动小组件 -->
    <DoctorConsultWidget />
  </div>
</template>

<script setup>
import DoctorConsultWidget from './components/DoctorConsultWidget.vue'
</script>

