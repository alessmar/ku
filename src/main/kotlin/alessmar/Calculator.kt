package alessmar

import javafx.application.Application
import javafx.stage.Stage
import javafx.scene.layout.GridPane
import javafx.geometry.Pos
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.layout.FlowPane
import javafx.scene.control.MenuBar
import javafx.scene.control.ToggleGroup
import javafx.scene.control.Menu
import javafx.scene.control.RadioMenuItem
import javafx.scene.text.Text
import javafx.scene.control.TextField
import javafx.scene.control.Label
import javafx.scene.control.Button
import javafx.stage.FileChooser
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.nio.file.Files
import java.io.File

class Calculator : Application() {
	val width = 700.0
	val height = 200.0
	val defaultAlgo = Hash.SHA256

	companion object {
		@JvmStatic
		fun main(args: Array<String>) {
			launch(Calculator::class.java)
		}
	}

	override fun start(stage: Stage?) {
		val hashGroup = ToggleGroup()
		val optGroup = ToggleGroup()

		val fileNameValue = Text("")
		val hashLabel = createLabel(defaultAlgo.name)
		val hashValue = TextField("")
		hashValue.setEditable(false)
		hashValue.setPrefWidth(width - 150)

		val openButton = Button("Choose a file")
		openButton.setOnAction {
			val file = FileChooser().showOpenDialog(stage)
			fileNameValue.setText(file.getName())
			fileNameValue.setUserData(file)

			val useUppercase: Boolean = optGroup.getSelectedToggle().getUserData() as Boolean
			val hash: String = hashGroup.getSelectedToggle().getUserData() as String

			hashLabel.setText("${hash}:")
			hashValue.setText(Hash.valueOf(hash).checksum(file, useUppercase))
		}

		val grid = GridPane()
		grid.setAlignment(Pos.TOP_LEFT)
		grid.setHgap(10.0)
		grid.setVgap(10.0)
		grid.setPadding(Insets(10.0))
		grid.addRow(0, openButton)
		grid.addRow(1, createLabel("Filename"), fileNameValue)
		grid.addRow(2, hashLabel, hashValue)

		val hashMenu = Menu("Algorithm")

		for (hash in Hash.values()) {
			val hashName = hash.name
			val hashMenuItem = RadioMenuItem(hashName)
			hashMenuItem.setUserData(hashName)
			hashMenuItem.setToggleGroup(hashGroup)
			hashMenuItem.setSelected(hashName == defaultAlgo.name)

			hashMenu.getItems().addAll(hashMenuItem)
		}
		hashGroup.selectedToggleProperty().addListener { _, oldT, newT ->
			if (oldT != newT) {
				newT?.let {
					val hash: String = newT.getUserData() as String
					hashLabel.setText("${hash}:")

					val f: File = fileNameValue.getUserData() as File
					val useUppercase: Boolean = optGroup.getSelectedToggle().getUserData() as Boolean
					hashValue.setText(Hash.valueOf(hash).checksum(f, useUppercase))
				}
			}
		}

		val optMenu = Menu("Options")

		for ((optionName, value) in mapOf("Uppercase" to true, "Lowercase" to false)) {
			val optMenuItem = RadioMenuItem(optionName)
			optMenuItem.setUserData(value)
			optMenuItem.setToggleGroup(optGroup)
			optMenuItem.setSelected(optionName == "Lowercase")

			optMenu.getItems().addAll(optMenuItem)
		}
		optGroup.selectedToggleProperty().addListener { _, oldT, newT ->
			if (oldT != newT) {
				newT?.let {
					val useUppercase: Boolean = newT.getUserData() as Boolean
					val hash = hashValue.getText()

					hashValue.setText(if (useUppercase) hash.toUpperCase() else hash.toLowerCase())
				}
			}
		}

		val menuBar = MenuBar()
		menuBar.getMenus().addAll(hashMenu, optMenu)
		menuBar.prefWidthProperty().bind(stage?.widthProperty())

		val flowPane = FlowPane()
		flowPane.getChildren().addAll(menuBar, grid)

		val scene = Scene(flowPane, width, height)
		stage?.setTitle("ChecksumUtility")
		stage?.setScene(scene)
		stage?.show()
	}

	fun createLabel(key: String): Label {
		return Label("${key}:")
	}
}
