package com.cameleon.photo.manager.bean.dto

data class PhotoMetadata(val cameraMake: String, val cameraModel: String, val focalLength: Double? = null, val apertureFNumber: Double? = null, val isoEquivalent: Int? = null, val exposureTime: String? = null)