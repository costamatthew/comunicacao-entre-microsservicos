import User from '../model/User.js'

class UserRepository {
  async findById(id) {
    try {
      return await User.findOne({ where: { id } })
    } catch (errors) {
      console.error(error.message)
      return null
    }
  }

  async findByEmail(email) {
    try {
      return await User.findOne({ where: { email } })
    } catch (errors) {
      console.error(error.message)
      return null
    }
  }
}

export default new UserRepository()
