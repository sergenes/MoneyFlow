package data

import co.touchlab.stately.ensureNeverFrozen
import com.prof18.moneyflow.db.AccountTable
import com.prof18.moneyflow.db.CategoryTable
import com.prof18.moneyflow.db.MonthlyRecapTable
import com.prof18.moneyflow.db.TransactionTable
import data.db.DatabaseSource
import data.db.model.InsertTransactionDTO
import data.db.model.TransactionType
import domain.entities.BalanceRecap
import domain.entities.Category
import domain.entities.MoneyTransaction
import domain.entities.TransactionTypeUI
import domain.mapper.mapToInsertTransactionDTO
import domain.repository.MoneyRepository
import kotlinx.coroutines.flow.*
import presentation.CategoryIcon
import presentation.addtransaction.TransactionToSave
import utils.Utils.formatDate

class MoneyRepositoryImpl(private val dbSource: DatabaseSource): MoneyRepository {

    private var allTransactions: Flow<List<TransactionTable>> = emptyFlow()
    private var allCategories: Flow<List<CategoryTable>> = emptyFlow()
    private var monthlyRecap: Flow<MonthlyRecapTable> = emptyFlow()
    private var account: Flow<AccountTable> = emptyFlow()

    init {
        ensureNeverFrozen()
        allTransactions = dbSource.selectAllTransaction()
        allCategories = dbSource.selectAllCategories()
        monthlyRecap = dbSource.selectCurrentMonthlyRecap()
        account = dbSource.selectCurrentAccount()
    }

    override suspend fun getBalanceRecap(): Flow<BalanceRecap> {
        return monthlyRecap.combine(account) { monthlyRecap: MonthlyRecapTable, account: AccountTable ->
            BalanceRecap(
                totalBalance = account.amount,
                monthlyIncome = monthlyRecap.incomeAmount,
                monthlyExpenses = monthlyRecap.outcomeAmount
            )
        }
    }

    override suspend fun getLatestTransactions(): Flow<List<MoneyTransaction>> {
        return allTransactions.map {
            it.map {transaction ->

                val transactionTypeUI = when (transaction.type) {
                    TransactionType.INCOME -> TransactionTypeUI.INCOME
                    TransactionType.OUTCOME -> TransactionTypeUI.EXPENSE
                }

                MoneyTransaction(
                    id = transaction.id,
                    title = transaction.description ?: "",
                    amount = transaction.amount,
                    type = transactionTypeUI,
                    formattedDate = transaction.dateMillis.formatDate()
                )
            }
        }
    }

    override suspend fun insertTransaction(transactionToSave: TransactionToSave) {
        dbSource.insertTransaction(transactionToSave.mapToInsertTransactionDTO())
    }

    override suspend fun getCategories(): Flow<List<Category>> {
        return allCategories.map {
            it.map {category ->
                Category(
                    id = category.id,
                    name = category.name,
                    icon = CategoryIcon.fromValue(category.iconName)
                )
            }
        }
    }
}



