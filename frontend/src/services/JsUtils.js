class JsUtils {
  cleanObject (obj) {
    for (const propName in obj) {
      if (Object.hasOwn(obj, propName) && !obj[propName]) {
        delete obj[propName]
      }
    }
    return obj
  }

  isObject (object) {
    return object != null && typeof object === 'object'
  };

  deepEquals (object1, object2) {
    const objKeys1 = Object.keys(object1)
    const objKeys2 = Object.keys(object2)

    if (objKeys1.length !== objKeys2.length) return false

    for (const key of objKeys1) {
      const value1 = object1[key]
      const value2 = object2[key]

      const isObjects = this.isObject(value1) && this.isObject(value2)

      if ((isObjects && !this.isDeepEqual(value1, value2)) ||
        (!isObjects && value1 !== value2)
      ) {
        return false
      }
    }
    return true
  }
}

export default new JsUtils()
