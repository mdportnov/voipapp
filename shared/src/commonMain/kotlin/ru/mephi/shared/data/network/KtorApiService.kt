package ru.mephi.shared.data.network

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*
import ru.mephi.shared.data.model.NameItem
import ru.mephi.shared.data.model.UnitM
import kotlin.coroutines.cancellation.CancellationException

class KtorApiService : BaseApiService {
    private var httpClient = KtorClientBuilder.createHttpClient()

    override suspend fun getUnitByCodeStr(codeStr: String): Resource<List<UnitM>?> {
        try {
            val units: List<UnitM> =
                httpClient.get {
                    url {
                        path("get_units_mobile_catalog.json")
                        parameter("filter_code_str", codeStr)
                    }
                }
            if (units.isEmpty())
                return Resource.Error.EmptyError(exception = EmptyUnitException())
            return Resource.Success(data = units)
        } catch (exception: Throwable) {
            return when (exception) {
                is NetworkException -> Resource.Error.NetworkError(exception = exception)
                is NotFoundException -> Resource.Error.NotFoundError(exception = exception)
                is ForbiddenException -> Resource.Error.ServerNotRespondError(exception = exception)
                is EmptyUnitException -> Resource.Error.EmptyError(exception = exception)
                else -> Resource.Error.UndefinedError()
            }
        }
    }

    override suspend fun getUsersByName(filterLike: String): Resource<UnitM> {
        try {
            val unitOfUsers: UnitM = httpClient.get {
                url {
                    path("get_subscribers_mobile.json")
                    parameter("filter_lastname", "LIKE|%$filterLike%")
                }
            } ?: return Resource.Error.NetworkError(exception = NotFoundException(filterLike))

            if (unitOfUsers.appointments.isNullOrEmpty()) {
                return Resource.Error.EmptyError(exception = NotFoundException(filterLike))
            }

            return Resource.Success(data = unitOfUsers)
        } catch (exception: Throwable) {
            return when (exception) {
                is NetworkException -> Resource.Error.NetworkError(exception = exception)
                is NotFoundException -> Resource.Error.NotFoundError(exception = exception)
                is ForbiddenException -> Resource.Error.ServerNotRespondError(exception = exception)
                is EmptyUnitException -> Resource.Error.EmptyError(exception = exception)
                else -> Resource.Error.UndefinedError()
            }
        }
    }

    override suspend fun getUnitsByName(filterLike: String): Resource<List<UnitM>> {
        try {
            val units: List<UnitM> = httpClient.get {
                url {
                    path("get_units_mobile_find.json")
                    parameter("filter_fullname", "LIKE|%$filterLike%")
                }
            } ?: return Resource.Error.NetworkError(exception = NetworkException())

            if (units.isNullOrEmpty())
                return Resource.Error.NotFoundError(exception = NotFoundException(filterLike))

            return Resource.Success(data = units)
        } catch (exception: Throwable) {
            return when (exception) {
                is NetworkException -> Resource.Error.NetworkError(exception = exception)
                is NotFoundException -> Resource.Error.NotFoundError(exception = exception)
                is ForbiddenException -> Resource.Error.ServerNotRespondError(exception = exception)
                is EmptyUnitException -> Resource.Error.EmptyError(exception = exception)
                else -> Resource.Error.UndefinedError()
            }
        }
    }

    override suspend fun getInfoByPhone(phone: String): Resource<List<NameItem>> {
        try {
            val nameItems: List<NameItem> = httpClient.get {
                url {
                    path("get_displayname.json")
                    parameter("line", phone)
                }
            } ?: return Resource.Error.NetworkError(exception = NetworkException())

            if (nameItems.isNullOrEmpty())
                return Resource.Error.NotFoundError(exception = NotFoundException(query = phone))
            return Resource.Success(data = nameItems)
        } catch (exception: Throwable) {
            return when (exception) {
                is NetworkException -> Resource.Error.NetworkError(exception = exception)
                is NotFoundException -> Resource.Error.NotFoundError(exception = exception)
                is ForbiddenException -> Resource.Error.ServerNotRespondError(exception = exception)
                is EmptyUnitException -> Resource.Error.EmptyError(exception = exception)
                else -> Resource.Error.UndefinedError()
            }
        }
    }
}

class EmptyUnitException : Exception() {
    override val message: String
        get() = "Пустой пункт"
}

class NetworkException : Exception() {
    override val message: String
        get() = "Интернет-соединение отсутствует"
}

class ForbiddenException : Exception() {
    override val message: String
        get() = "Доступ к ресурсу запрещён"
}

class NotFoundException(private val query: String) : Exception() {
    override val message: String
        get() = "По запросу \"${query}\" ничего не найдено"
}

class UndefinedErrorException : Exception() {
    override val message: String
        get() = "Что-то пошло не так"
}