```mermaid
classDiagram
    %% Main Activities
    class MainActivity {
    -SensorManager sensorManager
    -Sensor lightSensor
    -Button navigateButton
    -TextView gameMoneyInDis
    +onCreate()
    +onSensorChanged()
    +onOptionsItemSelected()
    }

    class MainEasyActivity {
        -ImageButton[] buttons
        -ArrayList<Integer> images
        -TextView timerTextView
        +onCreate()
        +onButtonClick()
        +startNewGame()
    }

    class MainRegularActivity {
        -ImageButton[] buttons
        -ArrayList<Integer> images
        -TextView timerTextView
        +onCreate()
        +onButtonClick()
        +startNewGame()
    }

    class MainHardActivity {
        -ImageButton[] buttons
        -ArrayList<Integer> images
        -TextView timerTextView
        +onCreate()
        +onButtonClick()
        +startNewGame()
    }

    class MainPlayWithFriends {
        -ImageButton[] buttons
        -ArrayList<Integer> images
        -TextView timerTextView
        -int currentPlayer
        +onCreate()
        +onButtonClick()
        +switchPlayer()
    }

    class MainComputerActivity {
        -ImageButton[] buttons
        -ArrayList<Integer> images
        -TextView timerTextView
        -boolean computerPlaying
        +onCreate()
        +onButtonClick()
        +computerMove()
    }

    %% Services and Helpers
    class MusicService {
        -MediaPlayer mediaPlayer
        +onCreate()
        +pauseMusic()
        +resumeMusic()
    }

    class GameDatabaseHelper {
        -SQLiteDatabase db
        +insertGame()
        +getAllGames()
        +setIconInMenu()
    }

    class LightSensorHelper {
        -static instance
        +handleLightChange()
    }

    %% Settings and Shop
    class SettingsActivity {
        -Switch soundSwitch
        -Spinner difficultySpinner
        -Spinner timeSpinner
        +onCreate()
        +saveSettings()
    }

    class MainShop {
        -TextView gameMoneyInDis
        +onCreate()
        +updateScoreDisplay()
    }

    %% Record Board
    class RecordBoardActivity {
        -ListView gameListView
        -GameDatabaseHelper dbHelper
        +onCreate()
        +showGameDetails()
    }

    %% Relationships
    MainActivity --> MainEasyActivity : starts
    MainActivity --> MainRegularActivity : starts
    MainActivity --> MainHardActivity : starts
    MainActivity --> MainPlayWithFriends : starts
    MainActivity --> MainComputerActivity : starts
    MainActivity --> SettingsActivity : starts
    MainActivity --> MainShop : starts
    MainActivity --> RecordBoardActivity : starts

    MainActivity --> MusicService : uses
    MainActivity --> LightSensorHelper : uses
    MainActivity --> GameDatabaseHelper : uses

    MainEasyActivity --> MusicService : uses
    MainRegularActivity --> MusicService : uses
    MainHardActivity --> MusicService : uses
    MainPlayWithFriends --> MusicService : uses
    MainComputerActivity --> MusicService : uses

    MainEasyActivity --> GameDatabaseHelper : uses
    MainRegularActivity --> GameDatabaseHelper : uses
    MainHardActivity --> GameDatabaseHelper : uses
    MainPlayWithFriends --> GameDatabaseHelper : uses
    MainComputerActivity --> GameDatabaseHelper : uses

    RecordBoardActivity --> GameDatabaseHelper : uses
    MainShop --> GameDatabaseHelper : uses

    SettingsActivity --> MusicService : controls

  