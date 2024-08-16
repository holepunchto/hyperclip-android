
# Bare on Android  
  
Hyperclip is an example of embedding Bare in an Android application using <https://github.com/holepunchto/bare-kit>.  
  
You need [Hyperclip-desktop](https://github.com/holepuncto/hyperclip-desktop) to send clipboard content from Desktop to Android.  
  
# Pre-requisites
Android SDK API level: >=24

# Setup Instructions  

 1. Clone this repository
 2. Update the submodules `git submodule update --init --recursive `
 2. Run `npm install -g bare-dev && npm i `
 3. Accept Android Licenses `bare-dev android sdk licenses accept`
 4. Replace `HYPERCLIP_DESKTOP_KEY` in app.js with the key you get from hyperclip-desktop 
 5. Build the App `bare-dev build` 
 6. Run the application `bare-dev android run`
## License  
  
Apache-2.0

