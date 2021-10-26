package repository

interface Repository<M: RepoModel> {

    fun findById(id: String): M?

    fun deleteById(id: String): Boolean
}