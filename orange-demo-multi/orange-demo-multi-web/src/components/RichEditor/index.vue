<template>
  <div class="uditor-class" ref="editor"></div>
</template>

<script>
import WEditor from 'wangeditor';

const defaultConfigs = {
  uploadImgServer: undefined,
  uploadFileName: 'imageFile',
  uploadImgMaxSize: 1 * 1024 * 1024,
  uploadImgShowBase64: true,
  uploadImgMaxLength: 5,
  uploadImgParams: undefined,
  uploadImgParamsWithUrl: true,
  withCredentials: true,
  uploadImgTimeout: 5000,
  uploadImgHeaders: undefined,
  uploadImgHooks: undefined,
  zIndex: 0,
  lang: undefined,
  pasteFilterStyle: true,
  pasteIgnoreImg: false,
  onchangeTimeout: 10,
  menus: [
    // 标题
    'head',
    // 粗体
    'bold',
    // 字号
    'fontSize',
    // 字体
    'fontName',
    // 斜体
    'italic',
    // 下划线
    'underline',
    // 删除线
    'strikeThrough',
    // 文字颜色
    'foreColor',
    // 背景颜色
    'backColor',
    // 插入链接
    'link',
    // 列表
    'list',
    // 对齐方式
    'justify',
    // 引用
    'quote',
    // 插入图片
    'image',
    // 撤销
    'undo',
    // 重复
    'redo'
  ]
}

export default {
  props: {
    /**
     * 绑定字段
     */
    value: {
      type: String
    },
    /**
     * 配置项，详情请参考wangEditor文档
     */
    config: {
      type: Object,
      default: () => {
        return defaultConfigs;
      }
    }
  },
  data () {
    return {
      editor: null
    }
  },
  methods: {
    getHtml () {
      return this.editor ? this.editor.txt.html() : undefined;
    }
  },
  computed: {
    getConfigs () {
      return {...this.config, ...defaultConfigs};
    }
  },
  mounted () {
    this.editor = new WEditor(this.$refs.editor);
    this.editor.customConfig = {...this.getConfigs};
    this.editor.customConfig.pasteTextHandle = (content) => {
      // content 即粘贴过来的内容（html 或 纯文本），可进行自定义处理然后返回
      return content;
    }
    this.editor.customConfig.linkImgCallback = (url) => {
    }
    this.editor.customConfig.linkCheck = (text, link) => {
      return true // 返回 true 表示校验成功
      // return '验证失败' // 返回字符串，即校验失败的提示信息
    }

    this.editor.customConfig.linkImgCheck = (src) => {
      return true // 返回 true 表示校验成功
      // return '验证失败' // 返回字符串，即校验失败的提示信息
    }
    // 失去焦点后更新数据
    this.editor.customConfig.onblur = (html) => {
      this.$emit('input', html);
    }

    this.editor.create();
    this.editor.txt.html(this.value);
  },
  watch: {
    value: {
      handler (newValue) {
        if (this.editor) this.editor.txt.html(this.value);
      },
      immediate: true
    }
  }
}
</script>

<style>
</style>
