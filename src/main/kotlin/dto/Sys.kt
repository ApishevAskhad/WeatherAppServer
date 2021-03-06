package dto

import com.google.gson.annotations.SerializedName

data class Sys(

	@field:SerializedName("country")
	val country: String? = null,

	@field:SerializedName("sunrise")
	val sunrise: Long? = null,

	@field:SerializedName("sunset")
	val sunset: Long? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("type")
	val type: Int? = null
)