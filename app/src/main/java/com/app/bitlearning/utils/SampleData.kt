/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.utils

import com.app.bitlearning.models.Camera
import com.app.bitlearning.models.CameraType
import com.app.bitlearning.models.CurrencyWithSymbol

object SampleData {

    val cameras = listOf(
        Camera(
            id = 1,
            name = "Fujifilm X-100V",
            type = CameraType.POINT_AND_SHOOT,
            price = 3899.99,
            currencyWithSymbol = CurrencyWithSymbol.USD,
            icon = "https://images.unsplash.com/photo-1627902474271-127e3bf091f0?q=80&w=400",
            description = "Compact and stylish point-and-shoot camera with a 23mm f/2 lens, 26.1MP sensor, and advanced film simulation modes. Perfect for street photography.",
            isEnabled = true,
            isVisible = true,
        ),
        Camera(
            id = 2,
            name = "Sony A7 IV",
            type = CameraType.MIRRORLESS,
            price = 2499.99,
            currencyWithSymbol = CurrencyWithSymbol.USD,
            icon = "https://images.unsplash.com/photo-1677505121779-e9ab421673bb?q=80&w=400",
            description = "Versatile full-frame mirrorless camera with 33MP sensor, excellent low-light performance, and professional video capabilities. Ideal for hybrid shooters.",
            isEnabled = true,
            isVisible = true,
        ),
        Camera(
            id = 3,
            name = "Nikon D850",
            type = CameraType.DSLR,
            price = 2796.95,
            currencyWithSymbol = CurrencyWithSymbol.USD,
            icon = "https://cdn.vjshop.vn/may-anh/dslr/nikon/d850/nikon-d8502-500x500.jpg",
            description = "High-resolution DSLR with 45.7MP sensor, exceptional dynamic range, and robust build quality. Perfect for landscape and studio photography.",
            isEnabled = true,
            isVisible = true,
        ),
        Camera(
            id = 4,
            name = "Fujifilm X-T5",
            type = CameraType.MIRRORLESS,
            price = 1699.95,
            currencyWithSymbol = CurrencyWithSymbol.USD,
            icon = "https://images.unsplash.com/photo-1708819057588-6e58615fc09f?q=80&w=400",
            description = "Compact APS-C mirrorless camera with 40MP sensor, film simulation modes, and excellent image quality. Great for street and travel photography.",
            isEnabled = true,
            isVisible = true,
        ),
        Camera(
            id = 5,
            name = "GoPro Hero 12",
            type = CameraType.ACTION,
            price = 399.99,
            currencyWithSymbol = CurrencyWithSymbol.USD,
            icon = "https://cdn.vjshop.vn/camera-hanh-dong/gopro/gopro-12/hero-12-creator-editon/gopro-hero-12-black-1-500x500.jpg",
            description = "Ultra-compact action camera with 5.3K video recording, advanced stabilization, and waterproof design. Perfect for adventure and sports photography.",
            isEnabled = true,
            isVisible = true,
        ),
        Camera(
            id = 6,
            name = "Leica Q2",
            type = CameraType.COMPACT,
            price = 4995.00,
            currencyWithSymbol = CurrencyWithSymbol.USD,
            icon = "https://cdn.vjshop.vn/may-anh/mirrorless/leica/leica-q2/leica-q2-chinh-hang10-500x500.jpg",
            description = "Premium compact camera with full-frame sensor, fixed 28mm lens, and exceptional build quality. The ultimate street photography companion.",
            isEnabled = true,
            isVisible = true,
        ),
        Camera(
            id = 7,
            name = "Polaroid Now+",
            type = CameraType.INSTANT,
            price = 149.99,
            currencyWithSymbol = CurrencyWithSymbol.USD,
            icon = "https://www.polaroid.com/_next/image?url=https%3A%2F%2Fcdn.sanity.io%2Fimages%2Fieogcuy1%2Fproduction%2F4f87d1cbfcded75dfbe6262aa91e56d0b8b6d99e-1500x1500.png%3Fw%3D500%26fm%3Dwebp%26q%3D90%26dpr%3D2&w=828&q=90",
            description = "Modern instant camera with classic Polaroid charm, manual controls, and smartphone connectivity. Create instant memories with analog appeal.",
            isEnabled = true,
            isVisible = true,
        ),
        Camera(
            id = 8,
            name = "Canon EOS 5D Mark IV",
            type = CameraType.FULL_FRAME,
            price = 2199.99,
            currencyWithSymbol = CurrencyWithSymbol.USD,
            icon = "https://cdn.vjshop.vn/may-anh/dslr/canon/canon-eos-5d-mark-iv/canon-eos-5d-mark-iv-500x500.jpg",
            description = "Professional full-frame DSLR with 30.4MP sensor, dual pixel autofocus, and weather sealing. A reliable workhorse for professional photographers.",
            isEnabled = true,
            isVisible = true,
        ),
        Camera(
            id = 9,
            name = "Olympus OM-D E-M1 Mark III",
            type = CameraType.MIRRORLESS,
            price = 1499.99,
            currencyWithSymbol = CurrencyWithSymbol.USD,
            icon = "https://binhminhdigital.com/StoreData/images/Product/may-anh-olympus-o-m-d-e-m1-mark-iii.jpg",
            description = "Micro Four Thirds mirrorless camera with 20.4MP sensor, excellent stabilization, and compact design. Perfect for travel and wildlife photography.",
            isEnabled = true,
            isVisible = true,
        ),
        Camera(
            id = 10,
            name = "Pentax K-3 Mark III",
            type = CameraType.DSLR,
            price = 1999.95,
            currencyWithSymbol = CurrencyWithSymbol.USD,
            icon = "https://camerabox.vn/wp-content/uploads/2025/05/pentax-k-3-mark-iii.png",
            description = "Weather-sealed APS-C DSLR with 25.7MP sensor, in-body stabilization, and rugged construction. Built for outdoor and adventure photography.",
            isEnabled = true,
            isVisible = true,
        ),
        Camera(
            id = 11,
            name = "Canon EOS R5",
            type = CameraType.MIRRORLESS,
            price = 3899.99,
            currencyWithSymbol = CurrencyWithSymbol.USD,
            icon = "https://images.unsplash.com/photo-1614746480983-377658e91422?q=80&w=400",
            description = "Professional full-frame mirrorless camera with 45MP resolution, 8K video recording, and advanced autofocus system. Perfect for professional photography and videography.",
            isEnabled = true,
            isVisible = true,
        ),
    )

    fun getCameraById(id: Int): Camera? = cameras.find { it.id == id }

    fun getCamerasByType(type: CameraType): List<Camera> = cameras.filter { it.type == type }

    fun getFeaturedCameras(): List<Camera> = cameras.take(6)
}
