import projectConfig from '@/core/config';
import { buildGetUrl } from '@/core/http/requestUrl.js';
import { formatDate, parseDate } from 'element-ui/src/utils/date-util';
import { mapMutations } from 'vuex';
import { getToken } from '@/utils';

/**
 * 上传文件组件相关方法
 */
const uploadMixin = {
  methods: {
    /**
     * 解析返回的上传文件数据
     * @param {String} jsonData 上传文件数据，[{name, downloadUri, filename}]
     * @param {Object} params 上传文件的参数
     * @returns {Array} 上传文件信息，[{name, downloadUri, filename, url}]
     */
    parseUploadData (jsonData, params) {
      let pathList = [];
      if (jsonData != null) {
        try {
          pathList = JSON.parse(jsonData);
        } catch (e) {
          console.error(e);
        }
      }

      return pathList.map((item) => {
        let downloadParams = {...params};
        downloadParams.filename = item.filename;
        return {
          ...item,
          url: this.getUploadFileUrl(item, downloadParams)
        }
      });
    },
    /**
     * 获得上传文件url列表
     * @param {*} jsonData 上传文件数据，[{name, downloadUri, filename}]
     * @param {*} params 上传文件的参数
     * @returns {Array} 文件url列表
     */
    getPictureList (jsonData, params) {
      let tempList = this.parseUploadData(jsonData, params);
      if (Array.isArray(tempList)) {
        return tempList.map(item => item.url);
      } else {
        return [];
      }
    },
    /**
     * 将选中文件信息格式化成json信息
     * @param {Array} fileList 上传文件列表，[{name, fileUrl, data}]
     */
    fileListToJson (fileList) {
      if (Array.isArray(fileList)) {
        return JSON.stringify(fileList.map((item) => {
          return {
            name: item.name,
            downloadUri: item.downloadUri || item.response.data.downloadUri,
            filename: item.filename || item.response.data.filename
          }
        }));
      } else {
        return undefined;
      }
    },
    /**
     * 获得上传文件url
     * @param {*} item 上传文件
     * @param {*} params 上传文件的参数
     */
    getUploadFileUrl (item, params) {
      if (item == null || item.downloadUri == null) {
        return null;
      } else {
        let menuIdJsonStr = window.sessionStorage.getItem('currentMenuId');
        let currentMenuId;
        if (menuIdJsonStr != null) {
          currentMenuId = (JSON.parse(menuIdJsonStr) || {}).data;
        }
        params.Authorization = getToken();
        params.MenuId = currentMenuId;
        return buildGetUrl(item.downloadUri, params);
      }
    },
    /**
     * 获得上传接口
     * @param {*} url 上传路径
     */
    getUploadActionUrl (url) {
      if (url != null && url[0] === '/') {
        url = url.substr(1);
      }
      return projectConfig.baseUrl + url;
    },
    /**
     * 上传文件是否图片文件
     * @param {*} file 上传文件
     */
    pictureFile (file) {
      if (['image/jpeg', 'image/jpg', 'image/png'].indexOf(file.type) !== -1) {
        return true;
      } else {
        this.$message.error('图片文件格式不正确，请重新选择');
        return false;
      }
    }
  },
  computed: {
    getUploadHeaders () {
      let token = getToken();
      return {
        Authorization: token
      }
    }
  }
};

const allowStatsType = [
  'time',
  'datetime',
  'day',
  'month',
  'year'
];
/**
 * 日期相关方法
 */
const statsDateRangeMixin = {
  methods: {
    /**
     * 根据输入的日期获得日期范围（例如：输入2019-12-12，输出['2019-12-12 00:00:00', '2019-12-12 23:59:59']）
     * @param {Date|String} date 要转换的日期
     * @param {String} statsType 转换类型（day, month, year）
     * @param {String} format 输出格式
     */
    getDateRangeFilter (date, statsType = 'day', format = 'yyyy-MM-dd HH:mm:ss') {
      if (date == null) return [];

      statsType = allowStatsType.indexOf(statsType) === -1 ? 'day' : statsType;
      date = date.substr(0, date.indexOf(' '));
      let tempList = date.split('-');
      let year = Number.parseInt(tempList[0]);
      let month = Number.parseInt(tempList[1]);
      let day = Number.parseInt(tempList[2]);
      if (isNaN(year) || isNaN(month) || isNaN(day)) {
        return [];
      }
      let tempDate = new Date(year, month - 1, day);
      // 判断是否正确的日期
      if (isNaN(tempDate.getTime())) return [];

      tempDate.setHours(0, 0, 0, 0);
      let retDate;
      switch (statsType) {
        case 'day':
          retDate = [
            new Date(tempDate),
            new Date(tempDate.setDate(tempDate.getDate() + 1))
          ];
          break;
        case 'month':
          tempDate.setDate(1);
          retDate = [
            new Date(tempDate),
            new Date(tempDate.setMonth(tempDate.getMonth() + 1))
          ];
          break;
        case 'year':
          tempDate.setDate(1);
          tempDate.setMonth(0);
          retDate = [
            new Date(tempDate),
            new Date(tempDate.setFullYear(tempDate.getFullYear() + 1))
          ]
      }

      retDate[1] = new Date(retDate[1].getTime() - 1);

      return [
        formatDate(retDate[0], format),
        formatDate(retDate[1], format)
      ];
    },
    /**
     * 格式化日期
     * @param {Date|String} date 要格式化的日期
     * @param {String} statsType 输出日期类型
     * @param {String} format 输入日期的格式
     */
    formatDateByStatsType (date, statsType = 'day', format = 'yyyy-MM-dd') {
      if (date == null) return undefined;
      statsType = allowStatsType.indexOf(statsType) === -1 ? 'day' : statsType;
      if (statsType === 'datetime') format = 'yyyy-MM-dd HH:mm:ss';
      
      let tempDate = ((date instanceof Date) ? date : parseDate(date, format));
      if (!tempDate) return undefined;
      switch (statsType) {
        case 'time':
          return formatDate(tempDate, 'HH:mm:ss');
        case 'datetime':
          return formatDate(tempDate, 'yyyy-MM-dd HH:mm:ss');
        case 'day':
          return formatDate(tempDate, 'yyyy-MM-dd');
        case 'month':
          return formatDate(tempDate, 'yyyy-MM');
        case 'year':
          return formatDate(tempDate, 'yyyy');
        default:
          return formatDate(tempDate, 'yyyy-MM-dd');
      }
    },
    /**
     * 获得统计类型中文名称
     * @param {String} statsType 统计类型（day, month, year）
     */
    getStatsTypeShowName (statsType) {
      statsType = allowStatsType.indexOf(statsType) === -1 ? 'day' : statsType;
      switch (statsType) {
        case 'day': return '日统计';
        case 'month': return '月统计';
        case 'year': return '年统计';
      }
    },
    /**
     * 获取统计类型字典列表
     * @param {Array} statsTypeList 统计类型列表
     */
    getAllowStatsTypeList (statsTypeList) {
      if (Array.isArray(statsTypeList) && statsTypeList.length > 0) {
        return statsTypeList.map((item) => {
          return {
            id: item,
            name: this.getStatsTypeShowName(item)
          }
        });
      } else {
        return [];
      }
    }
  }
}
/**
 * 页面缓存相关方法
 */
const cachePageMixin = {
  methods: {
    /**
     * 移除缓存页面
     * @param {*} name 缓存组件的名称
     */
    removeCachePage (name) {
      this.removeCachePage(name);
    },
    /**
     * 从跳转页面返回并且刷新当前页面时调用
     */
    onResume () {
    },
    ...mapMutations(['addCachePage', 'removeCachePage'])
  },
  created () {
    this.addCachePage(this.$options.name);
  },
  mounted () {
    this.$route.meta.refresh = false;
  },
  activated () {
    if (this.$route && this.$route.meta && this.$route.meta.refresh) {
      this.onResume();
    }
    this.$route.meta.refresh = true;
  }
}
/**
 * 缓存页面跳转页面相关方法
 */
const cachedPageChildMixin = {
  data () {
    return {
      // 是否刷新父页面
      refreshParentCachedPage: false
    }
  },
  beforeRouteLeave (to, from, next) {
    if (to.meta == null) to.meta = {};
    to.meta.refresh = this.refreshParentCachedPage;
    next();
  }
}

export {
  uploadMixin,
  statsDateRangeMixin,
  cachePageMixin,
  cachedPageChildMixin
}
