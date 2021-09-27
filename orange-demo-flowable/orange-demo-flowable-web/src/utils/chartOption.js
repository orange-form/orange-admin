const defaultLineChartOption = {
  grid: {
    left: '3%',
    right: '4%',
    bottom: '20px',
    containLabel: true
  },
  xAxis: {
    axisLabel: {
      interval: 0,
      showMaxLabel: true
    }
  },
  legend: {
    top: '3%'
  }
}

const defaultBarChartOption = {
  grid: {
    left: '3%',
    right: '4%',
    bottom: '20px',
    containLabel: true
  },
  xAxis: {
    axisLabel: {
      interval: 0,
      showMaxLabel: true
    }
  },
  legend: {
    top: '3%'
  }
}

const defaultPieChartOption = {
  grid: {
    left: '3%',
    right: '4%',
    bottom: '20px',
    containLabel: true
  },
  legend: {
    top: '3%'
  },
  series: {
    center: ['50%', '60%']
  }
}

export {
  defaultLineChartOption,
  defaultBarChartOption,
  defaultPieChartOption
}
