class JsUtils {

  cleanObject(obj) {
    for (let propName in obj) {
      if (obj.hasOwnProperty(propName) && !obj[propName]) {
        delete obj[propName];
      }
    }
    return obj;
  }

}

export default new JsUtils();
