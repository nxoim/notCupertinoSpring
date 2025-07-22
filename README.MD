# What?
Cupertino flavored Compose Multiplatform spring constructors that are familiar to iOS developers.

<video src="output.webm" controls muted loop></video>

# Why?
1. For funzies
2. Brain cell gain
3. Someone probably was looking for something like this for compose
4. i'm pretty sure no one else did this?

# Is it accurate?
I mean it seems like it. Specs seem to behave as expected.
I also edited the overscroll effect implementation to feel more accurate. The usage looks like
```kotlin
spring() // the default duration based spring with 500ms duration and 0 bounce
// or like
smooth(extraBounce = 0.3f) // btw all return the compose SpringSpec<T>
// or like
spring(duration = 200.milliseconds, bounce = 0.3f)
// or like
PhysicsBasedSpring.spring(response = 0.2, dampingFraction = 0.6f)
```

# Note:
NOT PRODUCTION READY CODE. FOR EDUCATIONAL PURPOSES ONLY
