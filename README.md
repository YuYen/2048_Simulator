# 2048 Simulator

The project try to solve 2048 game and implement the following strategies to move tile or generate tile.
* Min-Max with Alpha Beta Pruning
* Monte Carlo Prediction

Turns out Monte Carlo Prediction perform pretty well when tile was generated randomly. However, if the tile generated with specific strategy, the game would ended much sooner. Thus, both moving and generating processes affect the result. In short, player need both good strategy and good luck to play well in the game.  


## How to run Pre-Compiled jar file
On a Machine with java 8 or upper installed...

Usage:
```java -jar ./2048Simulator.jar [depth move_algo [gen_algo]]```
- depth: search depth, should be integer
- move_algo (moving algorithm): "a"->Alpha Beta Pruning, "m"->Monte Carlo Prediction "r"->Random
- gen_algo (generating algorithm): "a"->Alpha Beta Pruning, "r"->Random


Example: apply Monte Carlo Prediction with search depth=9 on moving stragey and new tile are random generated.

```java -jar ./2048Simulator.jar 9 m r```


## Strategy Description
### Type of States
State can simply be defined by the number of each corresponding tiles. Though it’s a one player’s game, player needs to consider the possible consequence of the generating tile. Thus, the game can be decomposed into two different type of states: 
* **Moving State**: Agent decide which direction the tile should go. Depend on the tile’s distribution agent will have at most four possible actions which are corresponding to up, down, left and right.
* **Generating State**: A new tile may be generated in any empty space. Generating was supposed to be randomized in the original game. Since generating state is not controlled by the agent, we can regard these state as the opponent’s chance to make action for determining the position of the new tile. 
 
### Value of State
The game will not arrive the end just in couple rounds, thus it’s not efficient to search all the way down to the terminal state for deciding a single action. Here, we define the state value with only the value of tiles and its distribution in the state. Also, the action reword is estimated by possible return state value after n-steps where n refers to the depth of search. 

* **Value of Tiles**: If a tile got merged, the number of the merged tiles and the new number will contribute to the state value. Through the above logic, we can use the following recurrence formula to calculate the value from a single tile.

  _V(k) = k + 2V(k/2)_, _V(0)=0_, _V(2)=2_

  In the equation, V(k) refers to the value of tile with number k in the state.  In this game possible k value could be only 0, 2<sup>1</sup>,2<sup>2</sup>,…2<sup>16</sup>. Following table shows the example of value mapping.

  | |0|2<sup>1</sup>|2<sup>2</sup>|2<sup>3</sup>|2<sup>4</sup>|2<sup>5</sup>|2<sup>6</sup>|2<sup>7</sup>|2<sup>8</sup>|2<sup>9</sup>|...|
  |:---|:---|:---|:---|:---|:---|:---|:---|:---|:---|:---|:---|
  |Number|0|2|4|8|16|32|64|128|256|512|...|
  |Value |0|2|8|24|64|160|384|896|2048|4608|...|


* **Value of Distribution**: One rule of thumb in the game is to keep the maximum value stay in the corner. Since the maximum value typically has the lower chance to be merged in a state, stay in the corner will reduce the chance that the tile become the barrier for the other smaller tiles. Furthermore, if the tiles are placed in a descending order, a sequence of tiles maybe merge consecutively. Thus, the length of the descending order sequence is another bonus criterion for the state. The value of the terminal state is modified as following equation.

  _V(S) = (L+1) * V(K)_  

  In the equation, _V(S)_ refers to the value of a state. _V(K)_ means the summation of all tile values in a state. _L_ is the length of the longest descending sequence. The following figure shows the example of the longest descending order sequence.
   
  <img align="center" src="https://github.com/YuYen/2048_Simulator/blob/assets/fig/longestDecSeq.png">



### Searching Algorithms

In each state, agent try to make an action to maximize action reword. Based on different assumption for the opponent's strategy, two types of strategies are implemented:
 
* **Min-Max search**: Suppose that the opponent always selects the action to minimize the possible state value for the agent, the agent must take the safest movement to ensure they can survive even in the worst case. Since only the extreme value needs to be considered, alpha-beta pruning can be applied to speed up the search process.

* **Monte Carlo search**: Suppose that the opponent may pick any possible action with equal chance, the agent will need to consider the expected return state value from all possible worlds.

<img align="center" src="https://github.com/YuYen/2048_Simulator/blob/assets/fig/MinMax_MonteCarlo.png">


## Performance Comparison

In this simulation, the following four scenarios are repeated 100 times. 
* Min-Max Moving vs. Random Generating
* Monte Carlo Moving vs. Random Generating
* Min-Max Moving vs. Min-Max Generating
* Monte Carlo Moving vs. Min-Max Generating

<img align="center" src="https://github.com/YuYen/2048_Simulator/blob/assets/fig/res.png">

 The figure shows the resulting distribution of terminal states for each strategy setup. Through the figure, we found that Monte Carlo searching perform better than Min-Max if the tile was random generated. The reason may be that Min-Max strategy is to conservative for random generating tile. One example that Min-Max fail to keep maximum value in the corner is presented in the following figure. Decision Table shows the Min-MAx search expect a low action reword after 2 steps due to the worst case anticipated. On the contrary, Mente Carlo predicts a much higher reword after few more steps.

<img align="center" src="https://github.com/YuYen/2048_Simulator/blob/assets/fig/failCase.png"> 
 
 Furthermore, once generating strategy changed to Min-Max, both searching methods perform worse than random generating cases. It simply indicates that the tile generating logic is definitely effect the final result. Thus, even player calculated each step carefully, the game may still end shortly.






