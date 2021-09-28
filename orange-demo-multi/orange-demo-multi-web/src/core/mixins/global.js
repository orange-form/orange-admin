import Vue from 'vue';
import Request from '@/core/http/request.js';
import { mapMutations, mapGetters } from 'vuex';

// 全局mixin对象
const globalMixin = {
  data () {
    return {
      isHttpLoading: false
    }
  },
  methods: {
    /**
     * 是否显示遮罩
     * @param {Boolean} isShow  是否显示
     */
    showMask (isShow) {
      isShow ? this.loadingManager.showMask() : this.loadingManager.hideMask();
    },
    /**
     * 判读用户是否有权限
     * @param {String} permCode  权限字
     */
    checkPermCodeExist (permCode) {
      if ((this.getUserInfo || {}).permCodeSet != null) {
        return this.getUserInfo.permCodeSet.has(permCode);
      } else {
        return this.getUserInfo.isAdmin;
      }
    },
    /**
     * 将输入的值转换成指定的类型
     * @param {Any} value
     * @param {String} type 要转换的类型（integer、float、boolean、string）
     */
    parseParams (value, type = 'string') {
      if (value == null) return value;
      switch (type) {
        case 'integer': return Number.parseInt(value);
        case 'float': return Number.parseFloat(value);
        case 'boolean': return (value === 'true' || value);
        default: return String(value);
      }
    },
    /**
     * 将输入值转换为执行的类型数组
     * @param {Array} value 输入数组
     * @param {String} type 要转换的类型（integer、float、boolean、string）
     */
    parseArrayParams (value, type = 'string') {
      if (Array.isArray(value)) {
        return value.map((item) => {
          switch (type) {
            case 'integer': return Number.parseInt(item);
            case 'float': return Number.parseFloat(item);
            case 'boolean': return (item === 'true' || item);
            default: return String(item);
          }
        });
      } else {
        return [];
      }
    },
    /**
     * 下载上传的文件
     * @param {*} url 下载文件的url
     * @param {*} fileName 下载文件名
     */
    downloadFile (url, fileName) {
      Request({
        url: url,
        method: 'get',
        responseType: 'blob',
        transformResponse: function (data) {
          return data;
        }
      }).then(res => {
        let data = res.data;
        if (res.status === 200 && data instanceof Blob) {
          let url = window.URL.createObjectURL(data);
          let link = document.createElement('a');
          link.style.display = 'none';
          link.href = url;
          link.setAttribute('download', fileName);
          document.body.appendChild(link);
          link.click();
          document.body.removeChild(link);
        } else {
          this.$message.error('下载文件失败');
        }
      }).catch(e => {
        let reader = new FileReader();
        reader.onload = () => {
          let jsonObj = JSON.parse(reader.result);
          this.$message.error((jsonObj || {}).errorMessage || '下载文件失败');
        }
        reader.readAsText(e);
      });
    },
    ...mapMutations(['setLoadingStatus'])
  },
  computed: {
    ...mapGetters(['getUserInfo'])
  },
  watch: {
    'loadingManager.loading': {
      handler: function (newValue) {
        this.isHttpLoading = (newValue != null);
      }
    }
  }
}

Vue.mixin(globalMixin);
