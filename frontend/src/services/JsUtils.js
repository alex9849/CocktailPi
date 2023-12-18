class JsUtils {
  cleanObject (obj) {
    for (const propName in obj) {
      if (Object.hasOwn(obj, propName) && !obj[propName]) {
        delete obj[propName]
      }
    }
    return obj
  }
}

export default new JsUtils()
