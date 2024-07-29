/* global Bare, BareKit */

Bare
    .on('suspend', () => console.log('suspended'))
    .on('resume', () => console.log('resumed'))
    .on('exit', () => console.log('exited'))

import Hyperswarm from "hyperswarm";
import Hypercore from "hypercore";
import fs from "bare-fs";

// We need to store hypercore in /data/data/APP_IDENTIFIER/folder
var dir = '/data/data/to.holepunch.bare.android/hyperclip';
// Check if the directory exists, delete and recreate if it already exists. Prevents issue while working with multiple hypercore
if (fs.existsSync(dir)) {
    // Remove the directory and its contents
    console.log("Dir exists");
    fs.rmSync(dir, {
        recursive: true,
        force: true
    });
}

// Create the directory
fs.mkdirSync(dir);


// Initialise Hyperswarm and Hypercore
// Replace HYPERCLIP_DESKTOP_KEY with the key you got from hyperclip desktop app.
// https://github.com/supersuryaansh/hyperclip-desktop
const swarm = new Hyperswarm()
const core = new Hypercore(dir, "HYPERCLIP_DESKTOP_KEY")
// Create RPC
const rpc = new BareKit.RPC((req) => {
    //can establish two-way communication here later
})

await core.ready()
const foundPeers = core.findingPeers()
swarm.join(core.discoveryKey)

swarm.on('connection', conn => core.replicate(conn))
// swarm.flush() will wait until *all* discoverable peers have been connected to
// It might take a while, so don't await it
// Instead, use core.findingPeers() to mark when the discovery process is completed
swarm.flush().then(() => foundPeers())

// This won't resolve until either
//    a) the first peer is found
// or b) no peers could be found
await core.update()

for await (const block of core.createReadStream({
    start: 0,
    live: true
})) {
    // Send a RPC 'ping' signal to MainActivity.java
    let req = rpc.request('ping')
    // Send data along with the ping. This contains clipboard content received from the desktop app.
    req.send(block)
    // Console log the latest data.
    console.log(`${block}`)
}


