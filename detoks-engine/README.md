# Blockchain Engineering 2023 - Token Transaction Engine II

This folder contains our implemented and working token transaction engine. This engine allows for a fast and lightweight solution for fast token transactions based on the [Trustchain](https://github.com/Tribler/kotlin-ipv8/blob/master/doc/TrustChainCommunity.md). We make use of an [IPv8](https://github.com/Tribler/kotlin-ipv8) overlay on which we've built forward. The concept of Trustchain is one used for communicating and bookkeeping, to make sure the transactions are communicated properly, and that also the integrity can be maintained and ensured. 

## Content

Our app has the following content:

- **Token generation:** With our app it is possible to generate tokens on a large scale. The purpose of this is to create some form of currency to send to other peers currently active, you will also be able to see what tokens you have, and which ones you've recieved. To generate a token, simply use the button to generate one, then, select a peer in the current peers list, and then hit send to give them a token! If for some reason you don't like one of the tokens you've generated, you can select them and delete them too, or just delete them all.
  
- **Visible peers:** We maintain a list of the peers currently in proximity. It's very easily possible to see what peers are currently around with their id's in a handy list. It's also possible to refresh, as other peers may have joined in as well during the usage.

- **Sending tokens to peers:** It is possible to send many tokens at once to other peers in the vicinity, this can be done individually to a scale of a thousand tokens per second

## The structure of the project

The project and the code can be found in the current folder, the ```trustchain-superapp/detoks-engine``` folder. Our code is split up into multiple files, with each having their own specific role to play. Most of the related code to the transaction engine can be found in the TransactionCommunty file, where we handle tasks such as message grouping. In addition, we split up the code handling the SQLite database management and the token management code up as well. In ```db``` the tasks regarding the former are handled, while in the ```manage_tokens``` folder the latter is done.

## Using the app

Whilst first enabling our app in the trustchain-superapp, you'll be greeted with the entrance screen of the detoks engine. This screen has some video, and a button to take you to the sending tokens page. This app has a couple of important parts, which we will all discuss here to ensure proper app usage. Firstly, the numbers on the top show:

- How many tokens you're currently in possesion of
- The percentage (%) of tokens recieved when sending
- The amount of tokens that are being sent per second, if applicable
- The latency between sending and recieving the tokens

These statistics are an important part of knowing if your token may or may no have arrived, and also to know how well the performance of the app is, which may be key for benchmarking. In addition in the list below, you can see the fellow peers that you're able to send the tokens to if you wish so. These peers are selectable by ID.

Lastly, the buttons on the bottom allow you to not only generate the tokens on a large scale (1000 or 100000), but also send them to a peer, this is done as follows:

1) Ensure you have some tokens to send, use the generate token buttons at the bottom to do so
2) Next, select a peer from the list to send tokens to by tapping on them, you may need to go back one screen and enter again to force a refresh
3) Determine if you want to send many tokens at once or some per second, and press the button
4) Afterwards the statistics on the top of the screen will update accordingly

## Design

When opening the application, you see a page with two buttons. These buttons both bring you to a different page. Manage tokens, where you can send and generate tokens, and benchmarks, where you can also see some statistics.

<img src="https://github.com/bbrockbernd/trustchain-superapp/blob/Documentation/detoks-engine/Screenshot_20230417_182253.png" width="180">

On the manage tokens page, you will find a list of all your tokens at the top, followed by a list of currently available peers. To create a new token, you can click on the "gen token" button located at the bottom of the page. Once you have accumulated enough tokens, you can send them to a selected peer by clicking on either the "send", "send 2", or "send 5" buttons, which will respectively send 1, 2, or 5 tokens. The application operates on the FIFO principle, meaning that the tokens generated first will be sent first. Lastly, you can refresh the page by clicking on the refresh button.

<img src="https://github.com/bbrockbernd/trustchain-superapp/blob/Documentation/detoks-engine/Screenshot_20230418_135848.png" width="180">          <img src="https://github.com/bbrockbernd/trustchain-superapp/blob/Documentation/detoks-engine/token%20manager.gif" width="180">

The benchmarks page displays various statistics related to the application. At the top, you can view the number of tokens that you currently possess. Following that, you can find the percentage of sent packets that have successfully reached the designated peer. The throughput, measured in tokens per second, is displayed below the percentage. At the end of the statistics, you see the latency. After the statistics, a list of the currently available peers is displayed. At the bottom of the screen, you can find buttons specifically meant for generating or sending a large number of tokens.

<img src="https://github.com/bbrockbernd/trustchain-superapp/blob/Documentation/detoks-engine/Screenshot_20230418_145945.png" width="180">          <img src="https://github.com/bbrockbernd/trustchain-superapp/blob/Documentation/detoks-engine/benchmark.gif" width="180">

### Do you want to add your own app?

- [Adding your own app to the TrustChain Super App](doc/AppTutorial.md)

## Performance

The statistics are all calculated for three different group sizes. These sizes are 1, 10 and 100.

### Latency
Below, the average latency plotted over time is displayed. Here it is clear what the optimal group size is. The latency for groups of size 10 never rises above the latency of other group sizes. Groups of size 100 start with a latency that is better than the latency for groups of size 1 but rises above it for a while at the end. Finally, the latency for groups of size 1 starts high but spikes at the end.

<img src="https://github.com/bbrockbernd/trustchain-superapp/blob/Documentation/detoks-engine/latency.png" width="400">

### Received packages
Below, you see the percentage of sent packets that have been received by a chosen peer over time. Depending on what you are looking for, there are two optimal sizes. The percentage of received packages for groups of size 1 gradually increases over time but only reaches 100% after significantly more time than the other sizes. For groups of size 100, the percentage of received packages does decrease for a while but reaches 100% the fastest out of all group sizes.

<img src="https://github.com/bbrockbernd/trustchain-superapp/blob/Documentation/detoks-engine/packetsreceived.png" width="400">

### Throughput
Lastly, you see the throughput plotted over time. The optimal group size, in this case, is 100, as it results in a throughput that stays the highest out of all groups. Next is group size 10, and last is group size 1.

<img src="https://github.com/bbrockbernd/trustchain-superapp/blob/Documentation/detoks-engine/Throughput.png" width="400">

### Flame chart
This flame chart shows the performance during sending 5000 tokens at a rate of 1000 tokens per second.
<img src="https://github.com/bbrockbernd/trustchain-superapp/blob/Documentation/detoks-engine/flamechart.jpeg" width="1200">

## Implementation

The core of the application is the CommunityAdapter.kt file. Here all communication over the TrustChainCommunity is handled. The following functions are all public and can be used by anyone that wants to use our implementation:

```sendTokens(amount: Int, peer: Peer)```: This function takes an integer and a peer as arguments. It then sends a number of tokens equal to that integer to the specified peer.

```injectTokens(tokens: List<String>)```: This function takes a list of tokens as an argument and adds a new token to it. In other words, this function handles token generation.

```setReceiveTransactionHandler(handler: ((transaction: Transaction) -> Unit))```: This function takes a handler as an argument and makes sure that this handler gets called for every transaction that is received.

```setReceiveAgreementHandler(handler: (transaction: Transaction) -> Unit)```: This function takes a handler as an argument and makes sure that this handler gets called for every agreement that is received.

```getPeers()```: This function returns a list of all currently available peers that you can communicate with.

```getTokens()```: This function returns a list of all your available tokens.

```getTokenCount()```: Returns the amount of tokens available

## Build

If you want to build an APK, run the following command:

```
./gradlew :app:buildDebug
```

The resulting APK will be stored in `app/build/outputs/apk/debug/app-debug.apk`.

## Install

You can also build and automatically install the app on all connected Android devices with a single command:

```
./gradlew :app:installDebug
```

*Note: It is required to have an Android device connected with USB debugging enabled before running this command.*

## Tests

Run unit tests:
```
./gradlew test
```

Run instrumented tests:
```
./gradlew connectedAndroidTest
```

## Code style

[Ktlint](https://ktlint.github.io/) is used to enforce a consistent code style across the whole project.

Check code style:
```
./gradlew ktlintCheck
```

Run code formatter:
```
./gradlew ktlintFormat
```

