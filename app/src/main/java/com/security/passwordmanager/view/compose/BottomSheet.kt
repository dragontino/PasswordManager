package com.security.passwordmanager.view.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.security.passwordmanager.BottomSheetShape
import com.security.passwordmanager.R
import com.security.passwordmanager.TitleWithSubtitle
import com.security.passwordmanager.model.ScreenType
import com.security.passwordmanager.model.Themes

data class BottomSheetItem(
    val image: ImageVector,
    var text: String,
    val id: Int = -1,
    val imageTintColor: Color = Color.Black,
    var imageBound: ImageBounds = ImageBounds.START,
    var showImage: Boolean = true
)

enum class ImageBounds {
    START,
    END
}



@Composable
fun BottomSheetContent(
    titleWithSubtitle: TitleWithSubtitle = TitleWithSubtitle(),
    drawBeautifulDesign: Boolean = false,
    vararg bottomItems: BottomSheetItem,
    onClickToBottomItem: (bottomItem: BottomSheetItem) -> Unit
) {
    BottomSheetContent(
        titleWithSubtitle,
        drawBeautifulDesign,
        bottomItems = bottomItems,
        onClickToBottomItem,
        additionalContent = null
    )
}



@Composable
private fun BottomSheetContent(
    titleWithSubtitle: TitleWithSubtitle = TitleWithSubtitle(),
    drawBeautifulDesign: Boolean = false,
    vararg bottomItems: BottomSheetItem,
    onClickToBottomItem: (bottomItem: BottomSheetItem) -> Unit,
    additionalContent: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .border(
                width = 1.dp,
                color = colorResource(android.R.color.darker_gray),
                shape = BottomSheetShape
            )
    ) {
        BottomSheetHeading(
            title = titleWithSubtitle.title,
            subtitle = titleWithSubtitle.subtitle,
            drawBeautifulDesign = drawBeautifulDesign
        )

        bottomItems.forEach { item ->
            BottomSheetItem(item, onClickToBottomItem)
        }

        additionalContent?.invoke()
    }
}



@Composable
fun ThemeBottomSheetContent(
    currentTheme: Themes,
    updateTheme: (newTheme: Themes) -> Unit,
    additionalContent: @Composable (() -> Unit)? = null
) {
    val bottomItems = Themes.values().mapIndexed { index, themes ->
        BottomSheetItem(
            image = Icons.Default.RadioButtonChecked,
            text = stringResource(themes.titleRes),
            id = index,
            imageTintColor = colorResource(R.color.raspberry),
            imageBound = ImageBounds.END,
            showImage = themes == currentTheme
        )
    }.toTypedArray()

    BottomSheetContent(
        bottomItems = bottomItems,
        onClickToBottomItem = { updateTheme(Themes.values()[it.id]) },
        additionalContent = additionalContent
    )
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
            .padding(top = dimensionResource(R.dimen.activity_horizontal_margin))
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
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier
                .padding(start = 16.dp)
                .fillMaxWidth()
        )

        if (subtitle.isNotBlank())
            Text(
                text = subtitle,
                color = colorResource(android.R.color.darker_gray),
                fontSize = 17.sp,
                modifier = Modifier
                    .padding(start = 16.dp, bottom = 4.dp)
                    .fillMaxWidth()
            )

        Divider(
            color = colorResource(android.R.color.darker_gray),
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}


@Composable
private fun ColumnScope.BottomSheetItem(
    bottomSheetItem: BottomSheetItem,
    onClick: (bottomItem: BottomSheetItem) -> Unit
) {
    Row(modifier = Modifier
        .clickable { onClick(bottomSheetItem) }
        .align(Alignment.Start)
        .padding(vertical = 16.dp)
        .fillMaxWidth()
    ) {

        if (bottomSheetItem.showImage && bottomSheetItem.imageBound == ImageBounds.START) {
            BottomSheetItemIcon(bottomSheetItem, Modifier.padding(horizontal = 16.dp))
        }
        else
            Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = bottomSheetItem.text,
            color = MaterialTheme.colors.onBackground,
            fontSize = 18.sp,
            modifier = Modifier.weight(2f)
        )

        if (bottomSheetItem.showImage && bottomSheetItem.imageBound == ImageBounds.END) {
            BottomSheetItemIcon(bottomSheetItem, modifier = Modifier.padding(end = 16.dp))
        }
    }
}


@Composable
private fun BottomSheetItemIcon(bottomSheetItem: BottomSheetItem, modifier: Modifier = Modifier) {
    Icon(
        imageVector = bottomSheetItem.image,
        contentDescription = bottomSheetItem.text,
        tint = bottomSheetItem.imageTintColor,
        modifier = modifier
            .scale(1.17f)
            .clip(CircleShape.copy(CornerSize(40)))
    )
}




object BottomSheetItems {
    const val editItemId = 1
    const val copyItemId = 2
    const val deleteItemId = 3

    @Composable
    fun screenType(screenType: ScreenType) = BottomSheetItem(
        text = stringResource(screenType.singleTitleRes),
        image = screenType.icon,
        imageTintColor = colorResource(R.color.raspberry),
        id = screenType.id
    )

    @Composable
    fun edit(text: String) = BottomSheetItem(
        text = text,
        image = Icons.Outlined.Edit,
        imageTintColor = colorResource(R.color.raspberry),
        id = editItemId
    )

    @Composable
    fun copy(text: String) = BottomSheetItem(
        text = text,
        image = Icons.Outlined.ContentCopy,
        imageTintColor = colorResource(R.color.raspberry),
        id = copyItemId
    )

    @Composable
    fun delete(text: String) = BottomSheetItem(
        text = text,
        image = Icons.Outlined.Delete,
        imageTintColor = colorResource(R.color.raspberry),
        id = deleteItemId
    )
}



@Preview
@Composable
fun BottomSheetContentPreview() {
    val imageTintColor = colorResource(R.color.raspberry)

    BottomSheetContent(
        TitleWithSubtitle(
            title = "Wrecked",
            subtitle = "Imagine Dragons"
        ),
        drawBeautifulDesign = true,
        bottomItems = arrayOf(
            BottomSheetItem(
                image = Icons.Outlined.AccountCircle,
                text = "Hello world!",
                imageTintColor = imageTintColor,
            ),
            BottomSheetItem(
                image = Icons.Outlined.CreditCard,
                text = "Bank Test!",
                imageBound = ImageBounds.END,
                imageTintColor = imageTintColor,
//                onClick = {}
            )
        )
    ) { }
}