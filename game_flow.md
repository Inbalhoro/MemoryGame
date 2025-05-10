```mermaid
flowchart TD
    onCreate([onCreate()])
    updateGameSettings([updateGameSettings()])
    startNewGame([startNewGame()])
    onButtonClick([onButtonClick()])
    setclickable([setclickable()])
    resetChoices([resetChoices()])
    showTimeDialog([showTimeDialog()])
    getTotalScore([getTotalScore()])
    saveScoreToSharedPreferences([saveScoreToSharedPreferences()])
    End([End])

    onCreate --> updateGameSettings
    onCreate --> startNewGame
    startNewGame --> onButtonClick
    onButtonClick --> setclickable
    onButtonClick --> resetChoices
    resetChoices --> showTimeDialog
    showTimeDialog --> saveScoreToSharedPreferences
    showTimeDialog --> getTotalScore
    showTimeDialog --> End
    resetChoices -- "if not all matched" --> onButtonClick
``` 