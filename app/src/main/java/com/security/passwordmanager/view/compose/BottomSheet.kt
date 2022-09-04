package com.security.passwordmanager.view.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.security.passwordmanager.R
import com.security.passwordmanager.viewmodel.SettingsViewModel

open class BottomSheetFragment(protected val settingsViewModel: SettingsViewModel)
    : BottomSheetDialogFragment() {

    private val headingBuffer = Array(2) { String() }
    private var beautifulDesign = false
    private val viewBuffer = ArrayList<BottomSheetItem>()

    private var anotherView: @Composable (() -> Unit)? = null

    companion object {
        const val TAG = "BottomSheetFragment"
    }

    override fun getTheme() = R.style.AppBottomSheetDialogTheme


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(inflater.context).apply {
        setContent {
            BottomSheet(
                titleText = headingBuffer.first(),
                subtitleText = headingBuffer.last(),
                drawBeautifulDesign = beautifulDesign && settingsViewModel.baseSettings.isUsingBeautifulFont,
                bottomSheetItems = viewBuffer,
                anotherView = anotherView
            )
        }
    }



    fun setHeading(
        titleText: String,
        subtitleText: String = "",
        beautifulDesign: Boolean = false
    ) {
        headingBuffer[0] = titleText
        headingBuffer[1] = subtitleText
        this.beautifulDesign = beautifulDesign
    }


    fun addView(
        image: ImageVector,
        @StringRes textRes: Int,
        imageTintColor: Color = Color.Black,
        imageBound: ImageBounds = ImageBounds.LEFT,
        showImage: Boolean = true,
        onClick: () -> Unit
    ) = addView(image, getString(textRes), imageTintColor, imageBound, showImage, onClick)


    fun addView(
        image: ImageVector,
        text: String,
        imageTintColor: Color = Color.Black,
        imageBound: ImageBounds = ImageBounds.LEFT,
        showImage: Boolean = true,
        onClick: () -> Unit
    ) {
        val view = BottomSheetItem(
            image = image,
            text = text,
            imageTintColor = imageTintColor,
            imageBound = imageBound,
            showImage = showImage,
            onClick = onClick
        )

        if (view !in viewBuffer)
            viewBuffer += view
    }


    protected fun addView(content: @Composable () -> Unit) {
        anotherView = content
    }


    fun show(fm: FragmentManager) = show(fm, TAG)



    @Composable
    private fun BottomSheet(
        titleText: String = "",
        subtitleText: String = "",
        drawBeautifulDesign: Boolean = false,
        bottomSheetItems: List<BottomSheetItem>? = null,
        anotherView: @Composable (() -> Unit)? = null
    ) {
        val bottomSheetCorner = dimensionResource(R.dimen.bottom_sheet_corner)

        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = colorResource(R.color.text_color),
                    shape = RoundedCornerShape(
                        topStart = bottomSheetCorner,
                        topEnd = bottomSheetCorner
                    )
                )
                .background(
                    color = colorResource(R.color.app_background_color),
                    shape = RoundedCornerShape(
                        topStart = bottomSheetCorner,
                        topEnd = bottomSheetCorner
                    )
                )
                .fillMaxWidth()
        ) {
            BottomSheetHeading(titleText, subtitleText, drawBeautifulDesign)
            Divider(
                color = colorResource(android.R.color.darker_gray),
                startIndent = 1.dp
            )

            bottomSheetItems?.forEach { bottomView -> BottomView(bottomView) }

            anotherView?.invoke()
        }
    }


    @Composable
    private fun BottomSheetHeading(
        titleText: String,
        subtitleText: String,
        drawBeautifulDesign: Boolean
    ) {
        if (titleText.isBlank()) return

        Column(
            modifier = Modifier
                .padding(dimensionResource(R.dimen.activity_horizontal_margin))
                .fillMaxWidth()
        ) {
            val titleTextSize = if (drawBeautifulDesign) 24.sp else 20.sp
            val fontFamily = if (drawBeautifulDesign)
                MaterialTheme.typography.subtitle1.fontFamily
            else null

            Text(
                text = titleText,
                fontSize = titleTextSize,
                fontFamily = fontFamily,
                color = colorResource(R.color.text_color),
                modifier = Modifier.fillMaxWidth()
            )

            if (subtitleText.isBlank()) return

            Text(
                text = subtitleText,
                color = colorResource(android.R.color.darker_gray),
                fontSize = 17.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }


    @Composable
    private fun BottomView(bottomSheetItem: BottomSheetItem) {
        TextField(
            value = bottomSheetItem.text,
            onValueChange = {},
            readOnly = true,
            leadingIcon = {
                if (bottomSheetItem.showImage && bottomSheetItem.imageBound == ImageBounds.LEFT)
                    BottomElementImage(bottomSheetItem.image, bottomSheetItem.text)
            },
            trailingIcon = {
                if (bottomSheetItem.showImage && bottomSheetItem.imageBound == ImageBounds.RIGHT)
                    BottomElementImage(bottomSheetItem.image, bottomSheetItem.text)
            },
            colors = TextFieldDefaults.textFieldColors(
                textColor = colorResource(R.color.text_color),
                backgroundColor = colorResource(R.color.app_background_color),
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            modifier = Modifier
                .padding(horizontal = 7.dp, vertical = 0.dp)
                .clickable(onClick = bottomSheetItem.onClick)
                .padding(vertical = 12.dp, horizontal = 8.dp)
                .fillMaxWidth()
        )
    }


    @Composable
    private fun BottomElementImage(image: ImageVector, text: String) {
        Image(
            imageVector = image,
            contentDescription = text,
            colorFilter = ColorFilter.tint(colorResource(R.color.raspberry)),
            modifier = Modifier
                .clip(CircleShape)
                .padding(end = 10.dp)
        )
    }
}


@Composable
fun ColumnScope.BottomSheetContent(
    title: String = "",
    subtitle: String = "",
    drawBeautifulDesign: Boolean = false,
    vararg bottomItems: BottomSheetItem
) {
    BottomSheetHeading(
        title = title,
        subtitle = subtitle,
        drawBeautifulDesign = drawBeautifulDesign
    )

    bottomItems.forEach { item ->
        BottomSheetItem(item)
    }
}



data class BottomSheetItem(
    val image: ImageVector,
    var text: String,
    val imageTintColor: Color = Color.Black,
    var imageBound: ImageBounds = ImageBounds.LEFT,
    var showImage: Boolean = true,
    val onClick: () -> Unit
)

enum class ImageBounds {
    LEFT,
    RIGHT
}


@Preview
@Composable
fun TestBottomSheet() {
    BottomSheet(
        titleText = "Wrecked",
        subtitleText = "Imagine Dragons",
        drawBeautifulDesign = true,
        bottomSheetItems = listOf(
            BottomSheetItem(
                image = Icons.Outlined.AccountCircle,
                text = "Hello world!",
                onClick = {}
            ),
            BottomSheetItem(
                image = Icons.Outlined.CreditCard,
                text = "Bank Test!",
                onClick = {}
            )
        )
    )
}


@Composable
private fun BottomSheet(
    titleText: String = "",
    subtitleText: String = "",
    drawBeautifulDesign: Boolean = false,
    bottomSheetItems: List<BottomSheetItem>? = null,
    anotherView: @Composable (() -> Unit)? = null
) {
    val bottomSheetCorner = dimensionResource(R.dimen.bottom_sheet_corner)

    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .border(
                width = 1.dp,
                color = colorResource(R.color.text_color),
                shape = RoundedCornerShape(
                    topStart = bottomSheetCorner,
                    topEnd = bottomSheetCorner
                )
            )
            .background(
                color = colorResource(R.color.app_background_color),
                shape = RoundedCornerShape(
                    topStart = bottomSheetCorner,
                    topEnd = bottomSheetCorner
                )
            )
            .fillMaxWidth()
    ) {
        BottomSheetHeading(titleText, subtitleText, drawBeautifulDesign)
        Divider(
            color = colorResource(android.R.color.darker_gray),
            startIndent = 1.dp
        )

        bottomSheetItems?.forEach { bottomView -> BottomSheetItem(bottomView) }

        anotherView?.invoke()
    }
}


@Composable
private fun ColumnScope.BottomSheetHeading(
    title: String,
    subtitle: String,
    drawBeautifulDesign: Boolean
) {
    if (title.isBlank()) return

    Column(
        modifier = Modifier
            .padding(dimensionResource(R.dimen.activity_horizontal_margin))
            .align(Alignment.Start)
            .fillMaxWidth()
    ) {
        val titleTextSize = if (drawBeautifulDesign) 24.sp else 20.sp
        val fontFamily = if (drawBeautifulDesign)
            MaterialTheme.typography.subtitle1.fontFamily
        else null

        Text(
            text = title,
            fontSize = titleTextSize,
            fontFamily = fontFamily,
            color = colorResource(R.color.text_color),
            modifier = Modifier.fillMaxWidth()
        )

        if (subtitle.isNotBlank())
            Text(
                text = subtitle,
                color = colorResource(android.R.color.darker_gray),
                fontSize = 17.sp,
                modifier = Modifier.fillMaxWidth()
            )

        Divider(
            color = colorResource(android.R.color.darker_gray),
            startIndent = 1.dp
        )
    }
}


@Composable
private fun ColumnScope.BottomSheetItem(bottomSheetItem: BottomSheetItem) {
    TextField(
        value = bottomSheetItem.text,
        onValueChange = {},
        readOnly = true,
        leadingIcon = {
            if (bottomSheetItem.imageBound == ImageBounds.LEFT)
                BottomElementImage(bottomSheetItem.image, bottomSheetItem.text)
        },
        trailingIcon = {
            if (bottomSheetItem.imageBound == ImageBounds.RIGHT)
                BottomElementImage(bottomSheetItem.image, bottomSheetItem.text)
        },
        textStyle = TextStyle(fontSize = 18.sp),
        colors = TextFieldDefaults.textFieldColors(
            textColor = colorResource(R.color.text_color),
            backgroundColor = colorResource(R.color.app_background_color),
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        modifier = Modifier
            .align(Alignment.Start)
            .padding(horizontal = 7.dp, vertical = 7.dp)
            .clickable {
                bottomSheetItem.onClick()
            }
            .fillMaxWidth()
    )
}


@Composable
private fun BottomElementImage(image: ImageVector, text: String) {
    Image(
        imageVector = image,
        contentDescription = text,
        colorFilter = ColorFilter.tint(colorResource(R.color.raspberry)),
        modifier = Modifier
            .scale(1.17f)
            .clip(CircleShape)
            .padding(end = 5.dp)
    )
}