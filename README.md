### Vimeo Mobile & TV Apps Group
#### Android Coding Exercise

BogusCode is an app that fetches and displays the first page of videos in the [Vimeo Staff Picks channel](https://developer.vimeo.com/api/endpoints). However, the codebase and user experience are suboptimal to say the least.  

You’ve inherited BogusCode and your task is to elevate it to your own standard of quality. Correct all of the issues you see, refactor as you see fit, and make BogusCode clean, clear, performant and scalable. 

We're looking for the following in no particular order:

* A bug-free app that displays the Staff Picks list
* Architectural clarity
* Software design that can scale
* Clear, functional, and responsive UI

Please modify the BogusCode/README.md file included in the code with your returned submission to contain a brief summary of the issues you identified, the changes you made to correct them, and rationale behind those changes. Please also include a brief summary and explanation of any significant architectural changes you made.

You may use **ONLY ONE** third party library<sup>1</sup> to assist you with the completion of the task. It is not a requirement, but you can choose one if you wish. If you use a third party library, please provide a rationale in your BogusCode/README.md modification. Submissions that leverage more than one third party library will be disqualified. 

Adding new features and functionality to the app is not necessary, but we appreciate candidates who go a little further and add in something extra. For example, if you're more comfortable with Kotlin, this could include converting the project to showcase your skills. That being said, we strongly suggest that you satisfy all of the requirements listed above before adding anything new. 

Feel free to send us any questions that might arise along the way. 

**Good luck!**

<sup>1</sup>&nbsp;*As a clarification, the Android Support Library is not considered to be a third party library. Feel free to use one third party library in addition to the Android Support Library. As a final note, we strongly discourage copying and pasting blocks of code from any other sources. However, if you can make a strong case for using someone else's code, please comment thoroughly to show an understanding in addition to crediting the source and the rationale.*


*******************************************************************

As part of this exercise, the main aspects of the app I wanted to target was Scalability, Responsive UI and Modularity.

As part of scalability, I chose to implement all network requests with Volley instead of an AsyncTask. By creating a custom wrapper around Volley, 
I was able to reuse the same class for making both json and image requests. An added advantage to using Volley over AsyncTask is avoiding the hassle 
of writing a lot of boiler plate code. Plus Volley has significantly better thread management, caching, retry mechanisms, error handling and support 
for multiple request objects, should we decide to scale in the future. AsyncTask is also more prone to memory leaks, which can be easily avoided here.

To target responsive UI, I implemented a recycler view with all the necessary views. Here we could avoid a whole lot of overhead by only loading data
the user could see on the screen currently. The recycler view and its components are placed in a Constraint Layout, helping me create a complex layout 
while sill maintaining a flat view hierarchy. This and the ability of the recycler view to reuse item views once they were scrolled away meant the app
wasn't consuming a lot of memory and would less likely run into an out of memory exception.

When it came to Modularity, my plan of action was to create data models for all necessary information that needed to be parsed. Also, by implementing
Pagination techniques, I didn't have to overload the app by requesting information from all pages, from the server. Only once a user has come to the 
end of a page, will the next page's information be requested.

I've also made a few small changes to the app:
- Updated the support libraries to use androidx.
- Removed the dependency on manually entered build SDK tools version.
- Handled configuration and screen rotation changes.
- Updated the gradle to the latest version.
- Updated the minimum sdk to support material designs, while compiling and targeting API 28.
- Implemented an on click functionality on every item that is generated in the recycler view. This will open the respective video
in the browser or the Vimeo Android app, if installed.
- Tested on multiple device emulators of different screen sizes to ensure the app loads and operates correctly.
- A couple of delighters like infinite scrolling, hiding views to make use of all available screen area, cancelling requests, etc.
- I've used the Gson library to help parse data from a json response object. I've also shown an implementation of downloading images with Glide,
but as I was limited to using only 1 3rd party library, I've commented it out in the ContentAdapter class.


Scope for improvement/development
As my time with working on this app was limited, I wanted to concentrate on the core features necessary. Here are a few enhancements to be considered in the future:
- Add support localization and internationalization and Rich text format.
- Add search functionality to search for videos.
- Currently the app implements and infinite scroller. This could be changed to have a button and search functionality to search videos from a particular page.
- Implement an in-app video player/floating window.
