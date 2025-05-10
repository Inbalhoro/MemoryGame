שם מחלקה: MainShop
תפקיד המחלקה: ניהול חנות הרקעים במשחק, רכישת רקעים, בחירת רקע, והצגת יתרת כסף.
תכונות:
· gameMoneyInDis - TextView שמציג את יתרת הכסף של השחקן.
· currentgameMoney - int, יתרת הכסף הנוכחית.
· buyButton1, buyButton2, buyButton3 - כפתורי רכישת רקעים.
· backgroundSelectorLayout - תצוגה לבחירת רקע.
· backgroundOptionsLayout - תצוגה אופקית של אפשרויות הרקע.
· BACKGROUND_KEYS - מערך מחרוזות, מזהי רקעים לרכישה.
· BACKGROUND_RESOURCES - מערך מזהי משאבים של רקעים.
פעולות:
· onCreate() - אתחול רכיבי המסך, הצגת יתרה, הגדרת מאזינים לכפתורים.
· setupBackgroundSelector() - מוסיף אפשרויות רקע שנרכשו + ברירת מחדל.
· addBackgroundOption(int) - מוסיף אפשרות רקע לבחירה.
· checkAndShowBackgroundSelector() - בודק אם להציג את בורר הרקעים.
· onBackgroundSelected(View) - שומר רקע נבחר ומעדכן UI.
· loadButtonState() - טוען מצב כפתורי רכישה (אם נרכשו).
· updateScoreDisplay(TextView, int) - מעדכן תצוגת יתרה.
· handlePurchase(Button, String, int) - מבצע רכישת רקע.

---

שם מחלקה: MainEasyActivity
תפקיד המחלקה: ניהול משחק הזיכרון ברמת קל, כולל לוגיקת המשחק, רקע, ניקוד, והגדרות.
תכונות:
· startTime, elapsedTime - זמנים למדידת משך המשחק.
· timerTextView - תצוגת זמן.
· handler, timerRunnable - עדכון זמן ריצה.
· isGameRunning - האם המשחק פעיל.
· buttons - מערך כפתורי המשחק.
· images - רשימת מזהי תמונות למשחק.
· imageResources - מערך מזהי תמונות.
· firstChoice, secondChoice, firstChoiceIndex, secondChoiceIndex - משתנים לבחירת קלפים.
· timeInNumbersS - זמן השהייה בין קלפים.
· currentMoneyTextView, currentMoney - יתרה (לא בשימוש עיקרי).
· isButtonFlipped, isButtonMatched - מעקב אחרי מצב קלפים.
· statusText - תצוגת סטטוס.
· resetButton - כפתור איפוס.
פעולות:
· onCreate() - אתחול רכיבים, הגדרות, רקע, מאזינים.
· updateGameSettings() - עדכון נושא, זמן, צליל.
· startMusicService(), stopMusicService() - ניהול מוזיקה.
· onButtonClick(int, int) - טיפול בלחיצת קלף.
· resetChoices() - איפוס בחירות.
· showTimeDialog() - הצגת דיאלוג סיום.
· getTotalScore(), saveScoreToSharedPreferences(int) - ניהול ניקוד.
· setclickable(boolean) - הפעלת/השבתת קליקים.
· startNewGame() - אתחול משחק חדש.

---

שם מחלקה: MainRegularActivity
תפקיד המחלקה: ניהול משחק הזיכרון ברמת רגיל, כולל לוגיקת המשחק, רקע, ניקוד, והגדרות.
תכונות:
· startTime, elapsedTime - זמנים למדידת משך המשחק.
· timerTextView - תצוגת זמן.
· handler, timerRunnable - עדכון זמן ריצה.
· isGameRunning - האם המשחק פעיל.
· buttons - מערך כפתורי המשחק.
· images - רשימת מזהי תמונות למשחק.
· imageResources - מערך מזהי תמונות.
· firstChoice, secondChoice, firstChoiceIndex, secondChoiceIndex - משתנים לבחירת קלפים.
· timeInNumbersS - זמן השהייה בין קלפים.
· isButtonFlipped, isButtonMatched - מעקב אחרי מצב קלפים.
· statusText - תצוגת סטטוס.
· resetButton - כפתור איפוס.
· settingsButtonOnlyHere - כפתור הגדרות (לא בשימוש עיקרי).
פעולות:
· onCreate() - אתחול רכיבים, הגדרות, רקע, מאזינים.
· updateGameSettings() - עדכון נושא, זמן, צליל.
· startMusicService(), stopMusicService() - ניהול מוזיקה.
· onButtonClick(int, int) - טיפול בלחיצת קלף.
· resetChoices() - איפוס בחירות.
· showTimeDialog() - הצגת דיאלוג סיום.
· getTotalScore(), saveScoreToSharedPreferences(int) - ניהול ניקוד.
· setclickable(boolean) - הפעלת/השבתת קליקים.
· startNewGame() - אתחול משחק חדש.

---

שם מחלקה: MainHardActivity
תפקיד המחלקה: ניהול משחק הזיכרון ברמת קשה, כולל לוגיקת המשחק, רקע, ניקוד, והגדרות.
תכונות:
· startTime, elapsedTime - זמנים למדידת משך המשחק.
· timerTextView - תצוגת זמן.
· handler, timerRunnable - עדכון זמן ריצה.
· isGameRunning - האם המשחק פעיל.
· buttons - מערך כפתורי המשחק.
· images - רשימת מזהי תמונות למשחק.
· imageResources - מערך מזהי תמונות.
· firstChoice, secondChoice, firstChoiceIndex, secondChoiceIndex - משתנים לבחירת קלפים.
· timeInNumbersS, timeofcards - זמן השהייה בין קלפים.
· isButtonFlipped, isButtonMatched - מעקב אחרי מצב קלפים.
· statusText - תצוגת סטטוס.
· resetButton - כפתור איפוס.
· navigateButton, navigateToFirstPageButton - כפתורי ניווט (לא בשימוש עיקרי).
פעולות:
· onCreate() - אתחול רכיבים, הגדרות, רקע, מאזינים.
· updateGameSettings() - עדכון נושא, זמן, צליל.
· onButtonClick(int, int) - טיפול בלחיצת קלף.
· resetChoices() - איפוס בחירות.
· showTimeDialog() - הצגת דיאלוג סיום.
· getTotalScore(), saveScoreToSharedPreferences(int) - ניהול ניקוד.
· setclickable(boolean) - הפעלת/השבתת קליקים.
· startNewGame() - אתחול משחק חדש. 