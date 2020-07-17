export const notEmptyString = v => !!v && v !== '' || 'Required';
export const minLengthString = (v, min) => !v || v.length >= min || ("Minimal length " + min);
export const maxLengthString = (v, max) => !v || v.length <= max || ("Maximal length " + max);
export const emailString = v => {
  const re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  return !v || re.test(String(v).toLowerCase()) || "E-Mail required";
};
