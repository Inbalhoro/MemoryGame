```mermaid
flowchart TD
    MainActivity((MainActivity))
    MainEasyActivity((MainEasyActivity))
    MainRegularActivity((MainRegularActivity))
    MainHardActivity((MainHardActivity))
    MainShop((MainShop))
    RecordBoardActivity((RecordBoardActivity))
    SettingsActivity((SettingsActivity))
    helpActivity((helpActivity))
    MainStart((MainStart))
    MainPlayWithFriends((MainPlayWithFriends))
    MainComputerActivity((MainComputerActivity))

    MainActivity --|navigate to| MainEasyActivity
    MainActivity --|navigate to| MainRegularActivity
    MainActivity --|navigate to| MainHardActivity
    MainActivity --|navigate to| MainShop
    MainActivity --|navigate to| RecordBoardActivity
    MainActivity --|navigate to| SettingsActivity
    MainActivity --|navigate to| helpActivity
    MainActivity --|navigate to| MainStart
    MainActivity --|navigate to| MainPlayWithFriends
    MainActivity --|navigate to| MainComputerActivity

    MainEasyActivity --|navigate to| MainActivity
    MainEasyActivity --|navigate to| MainShop
    MainEasyActivity --|navigate to| RecordBoardActivity
    MainEasyActivity --|navigate to| SettingsActivity
    MainEasyActivity --|navigate to| helpActivity
    MainEasyActivity --|navigate to| MainStart

    MainRegularActivity --|navigate to| MainActivity
    MainRegularActivity --|navigate to| MainShop
    MainRegularActivity --|navigate to| RecordBoardActivity
    MainRegularActivity --|navigate to| SettingsActivity
    MainRegularActivity --|navigate to| helpActivity
    MainRegularActivity --|navigate to| MainStart

    MainHardActivity --|navigate to| MainActivity
    MainHardActivity --|navigate to| MainShop
    MainHardActivity --|navigate to| RecordBoardActivity
    MainHardActivity --|navigate to| SettingsActivity
    MainHardActivity --|navigate to| helpActivity
    MainHardActivity --|navigate to| MainStart

    MainShop --|navigate to| MainActivity
    MainShop --|navigate to| SettingsActivity
    MainShop --|navigate to| RecordBoardActivity
    MainShop --|navigate to| helpActivity
    MainShop --|navigate to| MainStart

    RecordBoardActivity --|navigate to| MainActivity
    RecordBoardActivity --|navigate to| MainShop
    RecordBoardActivity --|navigate to| SettingsActivity
    RecordBoardActivity --|navigate to| helpActivity
    RecordBoardActivity --|navigate to| MainStart

    SettingsActivity --|navigate to| MainActivity
    SettingsActivity --|navigate to| MainShop
    SettingsActivity --|navigate to| RecordBoardActivity
    SettingsActivity --|navigate to| helpActivity
    SettingsActivity --|navigate to| MainStart

    helpActivity --|navigate to| MainActivity
    helpActivity --|navigate to| MainShop
    helpActivity --|navigate to| RecordBoardActivity
    helpActivity --|navigate to| SettingsActivity
    helpActivity --|navigate to| MainStart

    MainStart --|navigate to| MainActivity
    MainStart --|navigate to| MainShop
    MainStart --|navigate to| RecordBoardActivity
    MainStart --|navigate to| SettingsActivity
    MainStart --|navigate to| helpActivity

    MainPlayWithFriends --|navigate to| MainActivity
    MainComputerActivity --|navigate to| MainActivity
``` 