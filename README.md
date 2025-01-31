# Concentra browser

The goals of this browser are to

* Minimize distractions (aside from those in the web content itself)
* Maximize screen space used to display the web content
* Maintain the same level of useability as Chrome
  * autofills usernames + passwords
  * autofills forms
  * stays signed in to websites across sessions

# Why create yet another Browser?

I dislike Chrome on Android because it increases "engagement" by adding distractions. Aka "grabbing users by the eyeballs"!
I did not want to be afraid of opening the browser because my attention might be hijacked.

To make my case: behold the Chrome Android default page, which is littered with undesired text and image content.

![image](https://github.com/user-attachments/assets/e9d531f9-8a96-40f9-8b47-3c4f847d9f93)

# Why not use an existing browser other than Chrome?

There are many alternative browsers available on the Play store, they mostly focus on
* privacy (cookie deletion, etc.)
* ad-blocking
* speed
* AI (a recent development that appeared after I wrote this browser)

The most distraction free off the shelf browser that I could find is Firefox Focus. But it didn't fit
my needs because it is too privacy oriented. It automatically deletes cookies and browsing history at the end of each session.
So I would need to signin to websites with each session. Not going to happen!

# Implementation

It requires surprisingly little code to create a browser because the Android WebView is so powerful.

To paraphrase Newton "Standing on the shoulders of giants means you don’t have to start from scratch — just reach higher!"

## UX considerations

Removing all visible UI elements by default maximizes screen space and minimizes distractions. The question then is how can the user
open the browser UI on any web page? On a touch screen device it's obvious that this will involve a gesture that doesn't interfere
with the web content interactions.

After exploring more exotic options (e.g. tapping simultaneously with 2 fingers) I decided that the double-tap gesture is a good solution for opening the browser UI.
The implementation is trivial because Android Views have built-in support for the double-tap gesture.
Double-tapping doesn't interfere with the web content interactions, as long as the user doesn't double-tap on clickable content.
In practice this UX is highly effective.

## Comparison: Concentra browser vs Chrome

### Default page

Concentra has much less distracting elements.

| Concentra | Chrome  |
|----------|----------|
| ![image](https://github.com/user-attachments/assets/e4d1a8d3-d02c-4796-8caf-74f1176399a1) | ![image](https://github.com/user-attachments/assets/375183de-785e-4ac1-89a3-24dbaa529704) |

### Displaying a web page 

The difference is much less stark in this case. But still Chrome shows significantly less content because of the space used by its top bar.

In these screenshots the system UI bar (top of the screen) is blank. In reality this space contains information such as: time, battery level, notification icons, etc.

| Concentra | Chrome  |
|----------|----------|
| ![image](https://github.com/user-attachments/assets/8a0ae58f-d1c6-4520-8051-cb0e384df01d) | ![image](https://github.com/user-attachments/assets/df83185c-cbf5-4ec1-be19-e88a539493a7) |

# Known bugs

* The zoom level is not adjusted automatically when web content is too wide for a phone screen's width. This makes it uncomfortable to read fixed width content formatted for a desktop browser. A workaround is to share the link to Chrome
* Files are not downloaded when a link to them is opened (e.g. PDFs). A workaround is to share the link to Chrome
* A loading indicator is not displayed while a page is loading

# Ideas for future development (by order of priority)

1. Search in page
2. Support access to the camera and the microphone
3. Reload page
4. Move forward in browsing history
