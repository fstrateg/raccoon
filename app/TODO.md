ТЗ для ИИ-агента: вкладка «Расходы» (встраивание в текущий проект)
0) Важные правила

Никаких новых сущностей и таблиц. Используем существующую таблицу projects и Entity Project.

type = -1 означает расход.

Никаких категорий, штрафов, связей. Расходы редкие.

Без сторонних библиотек. Только AndroidX/Material.

Диалоги только кастомные (как у нас): .create(), dialog.show(), window bg transparent, кнопки внутри layout.

Архитектуру не ломать: Room → Repository → ViewModel → Fragment (LiveData).

1) Файлы, которые агент должен найти и изменить
   1.1 Room / DB слой

app/src/main/java/.../data/AppDatabase.java

app/src/main/java/.../data/Project.java

app/src/main/java/.../data/ProjectDao.java

1.2 Repository / ViewModel

app/src/main/java/.../data/ProjectRepository.java

app/src/main/java/.../ui/projects/ProjectViewModel.java
(или общий ViewModel, который сейчас отдает getProjects() и умеет insert/update/delete)

1.3 UI слой (расходы)

app/src/main/java/.../ui/expenses/ExpensesFragment.java

существующий adapter для проектов:

либо ProjectAdapter.java (если универсальный)

либо создать новый ExpenseAdapter.java, но копированием текущего ProjectAdapter с минимальными изменениями (не придумывать новый паттерн)

1.4 Layout ресурсы

res/layout/fragment_expenses.xml

res/layout/item_project.xml (если используется общий item, допускается переиспользовать)

Создать новые:

res/layout/dialog_add_expense.xml

res/layout/dialog_edit_expense.xml

res/layout/dialog_expense_options.xml

Drawable уже есть:

res/drawable/dialog_background.xml

res/drawable/status_button_active.xml

res/drawable/status_button_inactive.xml

1.5 Strings

res/values/strings.xml (у нас русский дефолт)

2) Изменения в DAO (ProjectDao.java)
   2.1 Список расходов

Добавить методы (строки в одну строку, без """, Java 11):

@Query("SELECT * FROM projects WHERE type = -1 ORDER BY updatedAt DESC")
LiveData<List<Project>> getExpensesSorted();
2.2 Баланс (проверить и привести к правилу)

Найти существующий метод getBalance() и убедиться, что он учитывает расходы.

Правило:

SUM(type=1) - SUM(type=-1)

Если getBalance() сейчас считает только type=1, заменить на:

@Query("SELECT COALESCE(SUM(CASE WHEN type = 1 THEN amount WHEN type = -1 THEN -amount ELSE 0 END), 0) FROM projects")
LiveData<Integer> getBalance();

(Если метод уже такой — не трогать.)

3) Repository (ProjectRepository.java)

Добавить метод:

public LiveData<List<Project>> getExpensesSorted() {
return projectDao.getExpensesSorted();
}

CRUD операции insert/update/delete уже есть и должны работать для расходов (потому что это тоже Project).

4) ViewModel
   Вариант A (предпочтительно): отдельный ExpensesViewModel

Если у тебя сейчас ProjectViewModel завязан на фильтры, агент не должен туда лепить расходы.

Создать файл:

app/src/main/java/.../ui/expenses/ExpensesViewModel.java

Наследование: AndroidViewModel

Поля:

ProjectRepository repository;

LiveData<List<Project>> expenses;

В конструкторе:

expenses = repository.getExpensesSorted();

Методы:

LiveData<List<Project>> getExpenses()

insert(Project p)

update(Project p)

delete(Project p)

Вариант B: использовать существующий ProjectViewModel

Если в проекте уже один универсальный ViewModel (и так задумано), тогда добавить метод:

public LiveData<List<Project>> getExpenses() { return repository.getExpensesSorted(); }

Но только если это не превращает ViewModel в помойку. Если есть сомнения — вариант A.

5) ExpensesFragment (ExpensesFragment.java)
   5.1 Экран

RecyclerView + FAB (как в Projects)

фон sc_background

5.2 Подключение адаптера

Либо переиспользовать ProjectAdapter (если он отображает title+amount и ничего не зависит от типа)

Либо скопировать в ExpenseAdapter и переименовать (не изобретать заново)

Подписка:

viewModel.getExpenses().observe(getViewLifecycleOwner(), list -> adapter.setProjects(list));

(если метод адаптера называется setProjects — оставить как есть, даже если там расходы)

5.3 UX по long tap

Long tap по элементу → кастомный dialog_expense_options.xml:

“Редактировать”

“Удалить”

Никаких статусов там нет.

5.4 FAB

FAB открывает showAddExpenseDialog()

6) Кастомные диалоги
   6.1 dialog_add_expense.xml

На базе dialog_add_project.xml, но:

заголовок: “Добавить расход”

поля: название + сумма

кнопки внутри рамки

6.2 dialog_edit_expense.xml

На базе dialog_edit_project.xml, но:

без блока статуса

заголовок: “Редактировать расход”

поля: название + сумма

6.3 dialog_expense_options.xml

На базе dialog_project_options.xml, но пункты:

“Редактировать”

“Удалить” (опасный цвет)

6.4 Логика диалогов (в ExpensesFragment)

showAddExpenseDialog():

валидировать title, amount > 0

создать Project:

type = -1

timestamps now

viewModel.insert(expense)

showEditExpenseDialog(Project expense):

изменить title/amount

type не менять (оставить -1)

updatedAt now

viewModel.update(expense)

showExpenseOptions(Project expense):

Edit → showEditExpenseDialog(expense)

Delete → viewModel.delete(expense)

6.5 Transparent window

Во всех диалогах:

dialog.show();
if (dialog.getWindow() != null) {
dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
}
7) strings.xml (русский дефолт)

Добавить ключи (если нет):

expenses_add_title = “Добавить расход”

expenses_edit_title = “Редактировать расход”

edit = “Редактировать”

delete = “Удалить”

expense_title_hint = “На что потратил”

amount уже есть

save/cancel уже есть

8) Критерии приёмки

Вкладка “Расходы” показывает только type=-1 записи, сортировка по updatedAt DESC

Добавление/редактирование/удаление работает без перезапуска

Dashboard баланс и прогресс меняются при операциях с расходами

Диалоги выглядят как в остальных вкладках: градиент + рамка, без серых Material панелей

Можно уйти в минус по балансу

Нет падений при открытии/закрытии диалогов