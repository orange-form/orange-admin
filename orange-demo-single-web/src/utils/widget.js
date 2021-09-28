import { Message } from 'element-ui';
import { treeDataTranslate } from '@/utils';

const DEFAULT_PAGE_SIZE = 10;

export class DropdownWidget {
  /**
   * 下拉组件（Select、Cascade、TreeSelect、Tree等）
   * @param {function () : Promise} loadDropdownData 下拉数据获取函数
   * @param {Boolean} isTree 是否是树数据
   * @param {String} idKey 键字段字段名
   * @param {String} parentIdKey 父字段字段名
   */
  constructor (loadDropdownData, isTree = false, idKey = 'id', parentIdKey = 'parentId') {
    this.loading = false;
    this.dirty = true;
    this.dropdownList = [];
    this.isTree = isTree;
    this.idKey = idKey;
    this.parentIdKey = parentIdKey;
    this.loadDropdownData = loadDropdownData;
    this.setDropdownList = this.setDropdownList.bind(this);
    this.onVisibleChange = this.onVisibleChange.bind(this);
  }
  /**
   * 重新获取下拉数据
   */
  reloadDropdownData () {
    return new Promise((resolve, reject) => {
      if (!this.loading) {
        if (typeof this.loadDropdownData === 'function') {
          this.loading = true;
          this.loadDropdownData().then(dataList => {
            this.setDropdownList(dataList);
            this.loading = false;
            this.dirty = false;
            resolve(this.dropdownList);
          }).catch(e => {
            this.setDropdownList([]);
            this.loading = false;
            reject(this.dropdownList);
          });
        } else {
          reject(new Error('获取下拉数据失败'));
        }
      } else {
        resolve(this.dropdownList);
      }
    });
  }
  /**
   * 下拉框显示或隐藏时调用
   * @param {Boolean} isShow 正在显示或者隐藏
   */
  onVisibleChange (isShow) {
    return new Promise((resolve, reject) => {
      if (isShow && this.dirty && !this.loading) {
        this.reloadDropdownData().then(res => {
          resolve(res);
        }).catch(e => {
          reject(e);
        });
      } else {
        resolve(this.dropdownList);
      }
    });
  }
  /**
   * 设置下拉数据
   * @param {Array} dataList 要显示的下拉数据
   */
  setDropdownList (dataList) {
    if (Array.isArray(dataList)) {
      this.dropdownList = this.isTree ? treeDataTranslate(dataList, this.idKey, this.parentIdKey) : dataList;
    }
  }
}

export class TableWidget {
  /**
   * 表格组件
   * @param {function (params: Object) : Promise} loadTableData 表数据获取函数
   * @param {function () : Boolean} verifyTableParameter 表数据获取检验函数
   * @param {Boolean} paged 是否支持分页
   * @param {Boolean} rowSelection 是否支持行选择
   * @param {String} orderFieldName 默认排序字段
   * @param {Boolean} ascending 默认排序方式（true为正序，false为倒序）
   * @param {String} dateAggregateBy 默认排序字段的日期统计类型
   */
  constructor (loadTableData, verifyTableParameter, paged, rowSelection, orderFieldName, ascending, dateAggregateBy) {
    this.currentRow = null;
    this.loading = false;
    this.oldPage = 0;
    this.currentPage = 1;
    this.oldPageSize = DEFAULT_PAGE_SIZE;
    this.pageSize = DEFAULT_PAGE_SIZE;
    this.totalCount = 0;
    this.dataList = [];
    this.orderInfo = {
      fieldName: orderFieldName,
      asc: ascending,
      dateAggregateBy: dateAggregateBy
    };
    this.paged = paged;
    this.rowSelection = rowSelection;
    this.searchVerify = verifyTableParameter || function () { return true; };
    this.loadTableData = loadTableData || function () { return Promise.resolve(); };
    this.onCurrentPageChange = this.onCurrentPageChange.bind(this);
    this.onPageSizeChange = this.onPageSizeChange.bind(this);
    this.onSortChange = this.onSortChange.bind(this);
    this.getTableIndex = this.getTableIndex.bind(this);
    this.currentRowChange = this.currentRowChange.bind(this);
  }
  /**
   * 表格分页变化
   * @param {Integer} newCurrentPage 变化后的显示页面
   */
  onCurrentPageChange (newCurrentPage) {
    this.loadTableDataImpl(newCurrentPage, this.pageSize).then(() => {
      this.oldPage = this.currentPage = newCurrentPage;
    }).catch(() => {
      this.currentPage = this.oldPage;
    });
  }
  /**
   * 表格分页每页显示数量变化
   * @param {Integer} newPageSize 变化后的每页显示数量
   */
  onPageSizeChange (newPageSize) {
    this.pageSize = newPageSize;
    this.currentPage = 1
    this.loadTableDataImpl(1, newPageSize).then(() => {
      this.oldPage = this.currentPage;
      this.oldPageSize = this.pageSize;
    }).catch(e => {
      this.currentPage = this.oldPage;
      this.pageSize = this.oldPageSize;
    });
  }
  /**
   * 表格排序字段变化
   * @param {String} prop 排序字段的字段名
   * @param {String} order 正序还是倒序
   */
  onSortChange ({ prop, order }) {
    this.orderInfo.fieldName = prop;
    this.orderInfo.asc = (order === 'ascending');
    this.refreshTable();
  }
  /**
   * 获取每一行的index信息
   * @param {Integer} index 表格在本页位置
   */
  getTableIndex (index) {
    return this.paged ? ((this.currentPage - 1) * this.pageSize + (index + 1)) : (index + 1);
  }
  /**
   * 当前选中行改变
   * @param {Object} currentRow 当前选中行
   * @param {Object} oldRow 老的选中行
   */
  currentRowChange (currentRow, oldRow) {
    this.currentRow = currentRow;
  }
  clearTable () {
    this.currentRow = null;
    this.oldPage = 0;
    this.currentPage = 1;
    this.totalCount = 0;
    this.dataList = [];
  }
  /**
   * 获取表格数据
   * @param {Integer} pageNum 当前分页
   * @param {Integer} pageSize 每页数量
   * @param {Boolean} reload 是否重新获取数据
   */
  loadTableDataImpl (pageNum, pageSize, reload = false) {
    return new Promise((resolve, reject) => {
      if (typeof this.loadTableData !== 'function') {
        reject();
      } else {
        // 如果pageSize和pageNum没有变化，并且不强制刷新
        if (this.paged && !reload && this.oldPage === pageNum && this.oldPageSize === pageSize) {
          resolve();
        } else {
          let params = {};
          if (this.orderInfo.fieldName != null) params.orderParam = [this.orderInfo];
          if (this.paged) {
            params.pageParam = {
              pageNum,
              pageSize
            }
          }
          this.loading = true;
          this.loadTableData(params).then(tableData => {
            this.dataList = tableData.dataList;
            this.totalCount = tableData.totalCount;
            this.loading = false;
            resolve();
          }).catch(e => {
            this.loading = false;
            reject(e);
          });
        }
      }
    });
  }
  /**
   * 刷新表格数据
   * @param {Boolean} research 是否按照新的查询条件重新查询（调用verify函数）
   * @param {Integer} pageNum 当前页面
   */
  refreshTable (research = false, pageNum = undefined, showMsg = false) {
    let reload = false;
    if (research) {
      if (typeof this.searchVerify === 'function' && !this.searchVerify()) return;
      reload = true;
    }

    if (Number.isInteger(pageNum) && pageNum !== this.currentPage) {
      this.loadTableDataImpl(pageNum, this.pageSize, reload).then(res => {
        this.oldPage = this.currentPage = pageNum;
        if (research && showMsg) Message.success('查询成功');
      }).catch(e => {
        this.currentPage = this.oldPage;
      });
    } else {
      this.loadTableDataImpl(this.currentPage, this.pageSize, true).catch(e => {});
    }
  }
}

export class UploadWidget {
  /**
   * 上传组件
   * @param {Integer} maxCount 最大上传数量
   */
  constructor (maxCount = 1) {
    this.maxCount = maxCount;
    this.fileList = [];
    this.onFileChange = this.onFileChange.bind(this);
  }
  /**
   * 上传文件列表改变
   * @param {Object} file 改变的文件
   * @param {Array} fileList 改变后的文件列表
   */
  onFileChange (file, fileList) {
    if (Array.isArray(fileList) && fileList.length > 0) {
      if (this.maxCount === 1) {
        this.fileList = [fileList[fileList.length - 1]];
      } else {
        this.fileList = fileList;
      }
    } else {
      this.fileList = undefined;
    }
    return this.fileList;
  }
}

export class ChartWidget {
  /**
   * 图表组件
   * @param {function (params) : Promise} loadTableData chart数据获取函数
   * @param {function () : Boolean} verifyTableParameter 数据参数检验函数
   * @param {Array} columns 数据列
   */
  constructor (loadTableData, verifyTableParameter, columns) {
    this.columns = columns;
    this.loading = false;
    this.dataEmpty = false;
    this.chartData = undefined;
    this.chartObject = undefined;
    this.dimensionMaps = new Map();
    this.chartSetting = undefined;
    this.searchVerify = verifyTableParameter || function () { return true; };
    this.loadTableData = loadTableData || function () { return Promise.resolve(); };
  }
  /**
   * 获取图表数据
   * @param {Boolean} reload 是否重新获取数据
   */
  loadChartDataImpl (reload = false) {
    return new Promise((resolve, reject) => {
      if (typeof this.loadTableData !== 'function') {
        reject();
      } else {
        if (!reload) {
          resolve();
        } else {
          this.loading = true;
          this.loadTableData().then(tableData => {
            this.chartData = {
              columns: this.columns,
              rows: tableData.dataList
            }
            this.loading = false;
            if (this.chartObject) this.chartObject.resize();
            resolve();
          }).catch(e => {
            console.error(e);
            this.loading = false;
            reject(e);
          });
        }
      }
    });
  }
  /**
   * 刷新图表数据
   * @param {Boolean} research 是否按照新的查询条件重新查询（调用verify函数）
   */
  refreshChart (research = false) {
    if (research) {
      if (typeof this.searchVerify === 'function' && !this.searchVerify()) return;
    }

    this.loadChartDataImpl(true).catch(e => {});
  }
}
