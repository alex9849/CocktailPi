import axios from 'axios'

const API_PATH = 'api/transfer/'

class TransferService {
  exportRecipes (exportRequest) {
    return axios.post(API_PATH + 'export', exportRequest, {
      responseType: 'blob'
    }).then(response => {
      const url = window.URL.createObjectURL(new Blob([response.data]))
      const link = document.createElement('a')
      link.href = url
      const disposition = response.headers['content-disposition']
      let filename = 'export.zip'
      if (disposition && disposition.indexOf('filename=') !== -1) {
        filename = disposition.split('filename=')[1].replace(/"/g, '')
      }
      link.setAttribute('download', filename)
      document.body.appendChild(link)
      link.click()
      link.remove()
      window.URL.revokeObjectURL(url)
    })
  }

  getRecipes () {
    return axios.post(API_PATH + 'export/recipes').then(res => res.data)
  }

  uploadImportFile (formData) {
    return axios.post(API_PATH + 'import', formData)
      .then(res => res.data)
  }
}

export default new TransferService()
