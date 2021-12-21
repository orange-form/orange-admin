import * as staticDict from '@/staticDict'

export default class DictionaryController {
  static dictUserStatus () {
    return new Promise((resolve) => {
      resolve(staticDict.UserStatus);
    });
  }
  static dictUserType () {
    return new Promise((resolve) => {
      resolve(staticDict.UserType);
    });
  }
}
