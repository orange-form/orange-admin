const pattern = {
  mobie: /^((\+?86)|(\(\+86\)))?(13[012356789][0-9]{8}|15[012356789][0-9]{8}|18[02356789][0-9]{8}|147[0-9]{8}|1349[0-9]{7})$/,
  english: /^[a-zA-Z]+$/,
  englishAndNumber: /^[a-zA-Z0-9]+$/
}

/**
 * 邮箱
 * @param str
 */
export function isEmail (str) {
  return /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((.[a-zA-Z0-9_-]{2,3}){1,2})$/.test(str)
}

/**
 * 手机号码
 * @param str
 */
export function isMobile (str) {
  return pattern.mobie.test(str)
}

/**
 * 电话号码
 * @param str
 */
export function isPhone (str) {
  return /^([0-9]{3,4}-)?[0-9]{7,8}$/.test(str)
}

export default {
  pattern
}
