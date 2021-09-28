<template>
  <div class="tags-panel">
    <i class="el-icon-arrow-left arrow left" @click="beginPos > 0 && beginPos--" />
    <i class="el-icon-arrow-right arrow right" @click="getEndTagPos >= panelWidth && beginPos++" />
    <div class="main-panel">
      <div class="scroll-box">
        <TagItem class="item" title="主页" :class="{active: getCurrentMenuId == null}" v-show="0 >= beginPos" :supportClose="false"
          @click.native="onTagItemClick(null)" @contextmenu.prevent.native="openMenu(null, $event)" />
        <TagItem class="item" v-for="(item, index) in tagList" :key="item.menuId" :title="item.menuName"
          :class="{active: item.menuId === getCurrentMenuId}" v-show="(index + 1) >= beginPos"
          @close="onTagItemClose(item)" @click.native="onTagItemClick(item)"
          @contextmenu.prevent.native="openMenu(item, $event)" />
      </div>
    </div>
    <div v-show="visible" @click.stop="onMenuMaskClick" @contextmenu="openMaskMenu"
      style="z-index: 99999; position: fixed; background: rgba(0, 0, 0, 0.01); width: 100vw; height: 100vh; top: 0px; left: 0px">
      <ul class="contextmenu" style="z-index: 99999; background: white;" :style="{left: left + 'px', top: top + 'px'}">
        <li @click="closeSelectTag">关闭</li>
        <li @click="closeOthersTags">关闭其他</li>
      </ul>
    </div>
  </div>
</template>

<script>
import { mapGetters, mapMutations } from 'vuex';
import TagItem from './tagItem.vue';
export default {
  props: {
    tagList: Array
  },
  components: {
    TagItem
  },
  data () {
    return {
      panelWidth: 0,
      beginPos: 0,
      visible: false,
      top: 0,
      left: 0,
      selectedItem: undefined
    }
  },
  methods: {
    openMenu (item, e) {
      this.visible = true;
      this.selectedItem = item;
      this.left = e.clientX;
      this.top = e.clientY;
    },
    openMaskMenu (e) {
      e.preventDefault();
    },
    onMenuMaskClick () {
      this.visible = false;
    },
    closeSelectTag () {
      if (this.selectedItem != null) this.onTagItemClose(this.selectedItem);
      this.visible = false;
    },
    closeOthersTags () {
      this.selectedItem ? this.closeOtherTags(this.selectedItem.menuId) : this.clearAllTags();
      this.visible = false;
    },
    onTagItemClose (item) {
      this.removeTag(item.menuId);
    },
    onTagItemClick (item) {
      this.setCurrentMenuId(item ? item.menuId : undefined);
    },
    setCurrentTag (id) {
      if (id == null) {
        this.beginPos = 0;
        return;
      }

      let curPos = -1;
      for (let i = 0; i < this.tagList.length; i++) {
        if (this.tagList[i].menuId === id) {
          curPos = (i + 1);
          break;
        }
      }

      if (curPos > 0) {
        if (curPos > this.getEndPos()) {
          let timer = null;
          timer = setInterval(() => {
            let endPos = this.getEndPos();
            if (endPos >= curPos) {
              clearInterval(timer);
            } else {
              this.beginPos++;
            }
          }, 10);
        }

        if (curPos < this.beginPos) this.beginPos = curPos;
      }
    },
    getEndPos () {
      let width = 0;
      let childList = this.$children;
      let endPos = 0;
      for (let i = this.beginPos; i < childList.length; i++) {
        width += childList[i].$el.offsetWidth;
        if (width >= this.panelWidth) break;
        endPos = i;
      }

      return endPos;
    },
    onSizeChange () {
      this.$nextTick(() => {
        this.panelWidth = this.$el.offsetWidth - 60;
      });
    },
    ...mapMutations(['removeTag', 'setCurrentMenuId', 'closeOtherTags', 'clearAllTags'])
  },
  computed: {
    getEndTagPos () {
      let width = 0;
      let childList = this.$children;
      for (let i = this.beginPos; i < childList.length; i++) {
        width += childList[i].$el.offsetWidth;
        // 间隔距离
        width += 5;
        if (width > this.panelWidth) {
          break;
        }
      }

      return width;
    },
    ...mapGetters(['getCurrentMenuId'])
  },
  mounted () {
    this.panelWidth = this.$el.offsetWidth - 60;
    window.addEventListener('resize', this.onSizeChange);
    // this.getLastPosition();
  },
  destroyed () {
    window.removeEventListener('resize', this.onSizeChange);
  },
  watch: {
    getCurrentMenuId: {
      handler (newValue) {
        this.setCurrentTag(newValue);
      },
      immediate: true
    }
  }
}
</script>

<style lang="scss" scoped>
  @import '@/assets/style/element-variables.scss';
  .tags-panel {
  }
  .main-panel {
    margin: 0px 30px;
  }
  .scroll-box {
    overflow: hidden;
    white-space: nowrap;
    display: flex;
    flex-wrap: nowrap;
    align-items: center;
    width: 100%;
    height: 50px;
  }
  .arrow {
    height: 50px;
    line-height: 50px;
    width: 30px;
    text-align: center;
    font-size: 14px;
    cursor: pointer;
    z-index: 100;
    box-sizing: border-box;
  }
  .arrow.left {
    float: left;
  }
  .arrow.right {
    float: right;
  }

  .contextmenu {
    margin: 0px;
    z-index: 2;
    position: fixed;
    list-style-type: none;
    padding: 5px 0px;
    border-radius: 5px;
    font-size: 12px;
    font-weight:  400;
    color: #333;
    box-shadow: 2px 2px 3px 0 rgba(0, 0, 0, 0.3);
  }

  .contextmenu li {
    margin: 0px;
    padding: 7px 16px;
    cursor: pointer;
  }

  .contextmenu li:hover {
    background: #eee;
  }

</style>
