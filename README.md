# DiceBot
State of the art Discord dice bot. 

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
Returns the result of 3d20 roll. Additionally shows if a critical hit or miss occurred according to the rules of [TDE5][2]. 

!pstats
-
Returns the players statistics of all !p rolls. 

!ru
-
Returns a random user of the voice channel. 

!info !help
-
Returns a link to this page. 

Licence
=
This software is licenced under the MIT licence. The full licence text is in [Licence][3]. 

[1]: https://www.ecma-international.org/ecma-262/10.0/
[2]: https://ulisses-regelwiki.de/
[3]: https://github.com/tobiasmiosczka/DiceBot/blob/master/LICENSE