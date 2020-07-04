# DiceBot
State-of-the-art Discord dice bot. 

Commands
=

!r [arg]
-
Returns the result of a roll as specified by the argument. Examples are: 
- !r 1d6
- !r 3d20 + 3
- !r (5d100 - 1d6) * 2

This command also supports the full [JavaScript/ECMAScript][1] language. 

!p 
-
Returns the result of 3d20 rolls and shows if a critical hit or miss occurred according to the rules of [TDE5][2]. 

!v [time to vote in seconds] [option1] [option2] ..
-
Starts a vote.

!rcu
-
Returns a random user of the voice channel. 

!rgu
-
Returns a random user of the guid.

!rco
-
Returns the user of the voice channel in random order.

!rgo
-
Returns the user of the guild in random order.

!help
-
Returns a list of all commands.

!info
-
Returns a link to this page. 

Licence
=
The full licence text is in [Licence][3]. 

[1]: https://www.ecma-international.org/ecma-262/10.0/
[2]: https://ulisses-regelwiki.de/
[3]: https://github.com/tobiasmiosczka/DiceBot/blob/master/LICENSE