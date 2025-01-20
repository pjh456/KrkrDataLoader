const { app, BrowserWindow } = require('electron')

const createWindow = () => {
  const win = new BrowserWindow({
    width: 800,
    height: 600,
    menuBarVisible: false,
    resizable: false
  })

  win.loadFile('pages/menu.html')
}

app.whenReady().then(() => {
  createWindow()
})