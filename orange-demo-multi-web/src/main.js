import '@/core/http';
import JSONbig from 'json-bigint';
import '@/components/Dialog';
import Vue from 'vue';
import ElementUI from 'element-ui';
// import 'element-ui/lib/theme-chalk/index.css'
import '@/assets/style/index.scss';
import '@/core/mixins/global.js';
import App from './App';
import router from './router';
import store from './store';
import TreeSelect from '@/components/TreeSelect';
import RichEditor from '@/components/RichEditor';
import InputNumberRange from '@/components/InputNumberRange';
import DateRange from '@/components/DateRange';
import FilterBox from '@/components/FilterBox';
import TableProgressColumn from '@/components/TableProgressColumn';
import VCharts from 'v-charts';

window.JSON = new JSONbig({storeAsString: true});

Vue.component('tree-select', TreeSelect);
Vue.component('rich-editor', RichEditor);
Vue.component('input-number-range', InputNumberRange);
Vue.component('date-range', DateRange);
Vue.component('filter-box', FilterBox);
Vue.component('table-progress-column', TableProgressColumn);

Vue.use(ElementUI);
Vue.use(VCharts);

Vue.config.productionTip = false;

/* eslint-disable no-new */
new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app');
