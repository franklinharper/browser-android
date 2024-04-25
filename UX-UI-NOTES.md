# Browser Actions

## Examples

Examples of Browser Actions are
* Share current link
* Open a search window
* Open an AI window
* Quit the browser (returns to the previous app without having to tap on back X times)

## UX overview

Scenario

1. Browser shows screen with 100% web content. No Browser UI is visible
2. User requests the Browser Action UI
3. Browser displays its Action UI
4. User selections an Action
5. Browser hides its Action UI
6. Browser executes the Action

## UX implementation

In the previous scenario the only step that isn't obvious is: "User requests the Browser Action UI".

How can this request be made and meet the following requirements?

1. There isn't any visible browser UI
2. The request mechanism must not interfere with the underlying Web page UI

Next steps

1. Try using a standard touch screen gesture
2. If standard gestures aren't satisfactory => try custom gesture (e.g. two finger tap)

### What are the possible interaction sources?

* Microphone (aka voice commands)
* Touch screen
* Bluetooth
* Camera
* Physical buttons (e.g. volume up/down, on/off button)

#### Pros/Cons for Microphone input (aka voice commands)

Pros
* natural way of giving commands

Cons
* High energy (always listening in case the user says something)
* Interferes with web apps that use the microphone 
* Socially awkward if other people are around
* Won't work well in noisy environments or if other people are talking

#### Pros/Cons for Touch Screen input

Touch screen gesture evaluation

* Tap => already used by the web page for opening links
* Long Press => slows the user down
* Directional Swipe => already used by the web for scrolling the page
* Bezel Swipe Left/Right => Used by the OS for back gesture
* Bezel Swipe Top => Used by the OS to exit full screen mode
* Bezel Swipe Bottom => Used by the OS for screenshots and copying text
* Two finger tap => Unused

Two finger tap looks like a good candidate!

#### Pros/Cons for Bluetooth input

This could be an hardware button that attaches physically to the phone. This button connects 
to the phone via Bluetooth.

Pros
* Doesn't interfere with Web page UX

Cons
* Requires hardware ($$$, battery, recharging)
* Changes physical dimensions of the phone
* Higher energy consumption

#### Pros/Cons for Camera input

Similar to Pros/Cons for Microphone input.

#### Pros/Cons for existing physical button input

Physical button evaluation

* volume up/down => interferes with changing the phone volume
* on/off button => interferes with turning the phone screen on/off


