import 'package:flutter/material.dart';
import '../components/styling.dart';
import 'edit_profile.dart';
import '../model/message_class.dart';
import '../components/appbar.dart';
import '../components/future_handler.dart';
import '../views/add_message_card.dart';
import '../views/message_card.dart';
import 'profile_screen.dart';
import '../backend/backend_singleton.dart';

/// A page for adding messages, viewing messages, and liking/disliking messages
class MessageBoardPage extends StatefulWidget {
  /// A page for adding messages, viewing messages, and liking/disliking messages
  const MessageBoardPage({super.key});

  @override
  State<MessageBoardPage> createState() => _MessageBoardPageState();
}

class _MessageBoardPageState extends State<MessageBoardPage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: rootAppBar("Message Board"),
      body: Stack(
        children: [
          Positioned(
            bottom: 0,
            top: 20,
            left: 20,
            right: 20,
            child: waitAndQuery<List<Message>>(
              future: BackendModel.instance.getAllMessages(),
              child: (List<Message> data) {
                return RefreshIndicator(
                  onRefresh: () {
                    return Future.delayed(
                        const Duration(), () => setState(() {}));
                  },
                  child: ListView.separated(
                    shrinkWrap: true,
                    physics: const BouncingScrollPhysics(),
                    itemCount: data.length +
                        2, // List<Message>.length + (1) add_message_card + (1) extra padding
                    itemBuilder: (context, index) {
                      if (index == 0) {
                        return AddMessageCard(() => setState(() {}));
                      }
                      if (index == data.length + 1) {
                        return const SizedBox(height: 1000);
                      }
                      return MessageCard(data[data.length - index], (id) {
                        data.removeWhere((element) => element.id == id);
                        setState(() {});
                      });
                    },
                    separatorBuilder: (BuildContext context, int index) {
                      if (index == 0) return const SizedBox();
                      return Divider(
                          thickness: 1,
                          color: ColorThemes.secondayColor,
                          height: 8);
                    },
                  ),
                );
              },
            ),
          ),
          Positioned(
            bottom: 20,
            right: 20,
            child: ElevatedButton(
              child: const Text('Edit Profile'),
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                      builder: (context) => const EditProfileScreen()),
                );
              },
            ),
          ),
          Positioned(
            bottom: 20,
            left: 20,
            child: ElevatedButton(
              child: const Text('View Profile'),
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                      builder: (context) =>
                          ProfileScreen(BackendModel.instance.userID!)),
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}
