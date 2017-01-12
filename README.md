# specter-koans

Specter Koans are a fun and easy way to get started with Specter (https://github.com/nathanmarz/specter)
no experience assumed or required.
Just follow the instructions below to start making tests pass!

## Running the Koans

clone this project locally
run `lein koan run`

## Usage

Once you've kicked off the runner, you'll see the output that shows which file needs to be edited and which assertion failed.

The source files you need to edit are prefixed with the sequence you should follow like 01_topic1.clj, 02_topic2.clj

Fill in the blanks to make the tests pass and think about why the aswer is what it is.

## Trying things out at the repl

since this is a proper leiningen project you can pop a repl with `lein repl` and play with the things you saw in the Koan to explore why it worked. You can even try modified versions of the forms in the file to experiment with a particular part of the API

### Bugs

Since it can be frustrating being stuck on an exercise due to issues with the training tool rather than your brain so great care is taken to ensure the highest quality koans.

Since this is a tool for learning we have to expand on the definition of a defect to include ambiguity in wording of koans, not allowing all possible answers, misleading or incorrect use of the API, and koans that encourage bad practices.

Please report any of these immediatley. Hit me up on Slack, Twitter, gmail, or any other channel you've come to know me through and tell me what I did wrong or let me know you've opened up a ticket.

If you've opened up a pull request for the fix to the issue I'll review it and be sure to put you on the contributors list.

### Contributing

Patches are encouraged!  Make sure the answer sheet still passes
(`lein koan test`), and send a pull request.

The file ideaboard.txt has lots of good ideas for new koans to start, or things
to add to existing koans.  So write some fun exercises, add your answers to
`resources/koans.clj`, and I'll get them in there!

Please follow the guidelines in
http://tbaggery.com/2008/04/19/a-note-about-git-commit-messages.html for
commmit messages, and put your code in a feature branch (not master) before
making the pull request. This makes patches easier to review.

Feel free to contact me if you have any questions or want more direction before you start pitching in.

### Contributors


### Credits

These exercises follow the same pattern as the Clojure koans https://github.com/functional-koans/clojure-koans/

So none of this would be possible without all the work that EdgeCase did creating the Ruby Koans, all the work Aaron Bedra did making Clojure koans simple and fun to use and improve upon, all the work Colin Jones did maintaining the Clojure koans repo, and of course all of the contributors to it.

Last but not least, credit goes to Nathan Marz who wrote the Specter library and is always helpful. I doubt these koans would be any good without his help validating the tests

## License

The use and distribution terms for this software are covered by the
Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
which can be found in the file epl-v10.html at the root of this distribution.
By using this software in any fashion, you are agreeing to be bound by
the terms of this license.
