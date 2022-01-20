# Dating Profile Order Experiment

## Introduction

#### This is a demo Android dating app written in Kotlin to fulfill the requirements of the following prompt:

> Our Product team want to experiment with the order of information in a user's profile, to see if first impressions are
important. They are looking to answer questions like: Should the profile photo be first? What if we placed the about me text
higher? Is the school important for a College aged demographic?
The task requires displaying a single profile at a time with the profile fields in the order defined by the configuration. There
should also be a button that allows you to navigate to the next user.
Don't worry too much about how it looks for now, the design team haven't had a chance to look at it yet. Feel free to adjust
the layout as you see fit.

## Demo
<img src="https://user-images.githubusercontent.com/23622104/131268101-a863117b-85bd-4c41-aaed-49b6d1d5a4b6.gif" width="360" height = "780">


## Architecture

### MVVM

> I based the architecture of this demo on the Model, View, ViewModel Android coding pattern. It is a great pattern to follow because it separates functionality into different layers. It also helps to modularizes code for reusability. This was the first pattern that I learned when I began developing Android apps, and one that I have become familiar with while working with.

### Room
> I utilized the MVVM architecture to tie in a Room Database. The `matched_users` database stores the data of other users whom the user matches with.

> I decided to implement this database for demonstration purposes to mock up a means to answer the hypothetical questions set forth by the prompt. I imagine, in a real life scenario, that the data will be stored in some sort of off device database such as a cloud database. From there, intelligence analysts can answer the hypothetical questions.

> Database info is accessible via the `MatchesViewModel` where functions such as `getTopSchool()` and `getPhotoCount()` can be called. As a bonus, I decided to store the total amount of time a user spent looking at a profile `getLongestInteractionTime()`. 

### API
> I decided to use Retrofit and OkHTTP to `GET` sample data from the API provided to me in the prompt. All of the data is easily accessible from the `HingeApi` class

## Libraries

>Room

>Retrofit

>OkHTTP

> Gson

> Timber

> Glide
